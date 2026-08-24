import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:5000';

const flowSuccess = new Counter('flow_success');
const flowFailure = new Counter('flow_failure');

export const options = {
    scenarios: {
        delivery_flow: {
            executor: 'shared-iterations',
            vus: 50,
            iterations: 50,
            maxDuration: '2m',
        },
    },

    thresholds: {
        checks: ['rate>0.95'],
        flow_success: ['count>0'],
        http_req_failed: ['rate<0.05'],
    },
};

function request(method, url, body = null) {
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    return http.request(
        method,
        `${BASE_URL}${url}`,
        body,
        params,
    );
}

// ---------------------------------------------------------
// PAYMENT
// ---------------------------------------------------------

function getPendingPayment(deliveryId) {
    const response = request(
        'GET',
        `/api/payment/payments?status=Pending&delivryId=${deliveryId ?? ""}`,
    );

    const valid = check(response, {
        'get pending payments - status 200': r =>
            r.status === 200,
    });

    if (!valid) {
        return null;
    }

    const payments = response.json();

    if (!payments || payments.length === 0) {
        return null;
    }

    return payments;
}

function waitForPayment(deliveryId, timeout = 30000) {
    const start = Date.now();

    while (Date.now() - start < timeout) {
        const payments = getPendingPayment(deliveryId);


       const payment = payments.find(
            item => item.deliveryId === deliveryId,
        );

        if (payment) {
            return payment;
        }

        sleep(1);
    }

    return null;
}

// ---------------------------------------------------------
// ASSIGNED DELIVERY
// ---------------------------------------------------------

function getDeliveries(deliveryId, status) {
    const response = request(
        'GET',
        `/api/delivery/deliveries?status=${status}&deliveryId=${deliveryId ?? null}`,
    );

    const valid = check(response, {
        'get assigned deliveries - status 200': r =>
            r.status === 200,

        'get assigned deliveries - has list': r =>
            Array.isArray(r.json().content),
    });

    if (!valid) {
        return [];
    }

    return response.json().content;
}

function waitForCompletedDelivery(deliveryId, timeout = 30000) {
    const start = Date.now();

    while (Date.now() - start < timeout) {
        const deliveries = getDeliveries(deliveryId, 'DELIVERED');

        const delivery = deliveries.find(
            item => item.id === deliveryId,
        );

        if (delivery) {
            return delivery;
        }

        sleep(1);
    }

    return null;
}

function waitForAssignedDelivery(deliveryId, timeout = 30000) {
    const start = Date.now();

    while (Date.now() - start < timeout) {
        const deliveries = getDeliveries(deliveryId, 'ASSIGNED');

        const delivery = deliveries.find(
            item => item.id === deliveryId,
        );

        if (delivery) {
            return delivery;
        }

        sleep(1);
    }

    return null;
}

// ---------------------------------------------------------
// LEDGER
// ---------------------------------------------------------

function getLedgerEntries() {
    const response = request(
        'GET',
        '/api/ledger/ledger-entries',
    );

    check(response, {
        'get ledger entries - status 200': r =>
            r.status === 200,
    });

    return response;
}

// ---------------------------------------------------------
// SETUP
// ---------------------------------------------------------

export function setup() {
    const response = request(
        'GET',
        '/api/delivery/drivers',
    );

    const valid = check(response, {
        'get drivers - status 200': r =>
            r.status === 200,

        'get drivers - has list': r =>
            Array.isArray(r.json()),
    });

    if (!valid) {
        throw new Error('Failed to get drivers');
    }

    const drivers = response.json();

    if (drivers.length === 0) {
        console.log('No drivers found. Creating 5 drivers...');

        const phones = [
            '+48123456701',
            '+48123456702',
            '+48123456703',
            '+48123456704',
            '+48123456705',
        ];

        for (let i = 0; i < 5; i++) {
            const createDriverResponse = request(
                'POST',
                '/api/delivery/drivers',
                JSON.stringify({
                    firstName: `TestDriver${i + 1}`,
                    lastName: 'K6',
                    phoneNumber: phones[i],
                }),
            );

            const created = check(createDriverResponse, {
                'create driver - status 201': r =>
                    r.status === 201,

                'create driver - has id': r =>
                    r.json('id') !== undefined,
            });

            if (!created) {
                throw new Error(
                    `Failed to create driver ${i + 1}`,
                );
            }
        }

        console.log('5 drivers created');
    } else {
        console.log(
            `Drivers already exist (${drivers.length}). Skipping creation.`,
        );
    }
}

// ---------------------------------------------------------
// DELIVERY FLOW
// ---------------------------------------------------------

export default function () {
    const suffix = `${__VU}-${__ITER}-${Date.now()}`;

    console.log(
        `Starting delivery flow ${suffix}`,
    );

    // ---------------------------------------------------------
    // 1. CREATE DELIVERY
    // ---------------------------------------------------------

    const deliveryResponse = request(
        'POST',
        '/api/delivery/deliveries',
        JSON.stringify({
            deliveryAddress: `Test Delivery ${suffix}`,
            distanceKm: 50.0,
            pickupAddress: `Test Pickup ${suffix}`,
        }),
    );

    const deliveryCreated = check(deliveryResponse, {
        'create delivery - status 200/201': r =>
            r.status === 200 || r.status === 201,

        'create delivery - has id': r =>
            r.json('id') !== undefined,
    });

    if (!deliveryCreated) {
        flowFailure.add(1);
        return;
    }

    const deliveryId = deliveryResponse.json('id');

    console.log(
        `Delivery created: ${deliveryId}`,
    );

    // ---------------------------------------------------------
    // 2. CONFIRM DELIVERY
    // ---------------------------------------------------------

    const confirmResponse = request(
        'PUT',
        `/api/delivery/deliveries/${deliveryId}/confirm`,
    );

    const confirmed = check(confirmResponse, {
        'confirm delivery - status 204': r =>
            r.status === 204,
    });

    if (!confirmed) {
        flowFailure.add(1);
        return;
    }

    // ---------------------------------------------------------
    // 3. WAIT FOR PAYMENT
    // ---------------------------------------------------------

    const payment = waitForPayment(deliveryId);

    const paymentFound = check(payment || {}, {
        'payment created': p =>
            p !== null,

        'payment has id': p =>
            p && p.id !== undefined,
    });

    if (!paymentFound) {
        console.error(
            `Payment not found for delivery ${deliveryId}`,
        );

        flowFailure.add(1);
        return;
    }

    const paymentId = payment.id;

    console.log(
        `Payment created: ${paymentId}`,
    );

    // ---------------------------------------------------------
    // 4. ACCEPT PAYMENT
    // ---------------------------------------------------------

    const acceptPaymentResponse = request(
        'PUT',
        `/api/payment/payments/${paymentId}/accept`,
    );

    const paymentAccepted = check(
        acceptPaymentResponse,
        {
            'accept payment - status 204': r =>
                r.status === 204,
        },
    );

    if (!paymentAccepted) {
        console.error(acceptPaymentResponse);
        flowFailure.add(1);
        return;
    }

    // ---------------------------------------------------------
    // 5. WAIT FOR DELIVERY ASSIGNMENT
    // ---------------------------------------------------------

    const assignedDelivery =
        waitForAssignedDelivery(deliveryId);

    const deliveryAssigned = check(
        assignedDelivery || {},
        {
            'delivery assigned': d =>
                d !== null,

            'assigned delivery has id': d =>
                d && d.id !== undefined,

            'assigned delivery has correct id': d =>
                d && d.id === deliveryId,
            
            'assigned delivery has ASSIGNED status': d =>
            d && d.status === 'ASSIGNED',
        },
    );

    if (!deliveryAssigned) {
        console.error(
            `Delivery ${deliveryId} was not assigned`,
        );

        flowFailure.add(1);
        return;
    }

    console.log(
        `Delivery assigned: ${deliveryId}`,
    );

    // ---------------------------------------------------------
    // 6. PICKUP DELIVERY
    // ---------------------------------------------------------

    const pickupResponse = request(
        'PUT',
        `/api/delivery/deliveries/${deliveryId}/pickup`,
    );

    const pickedUp = check(pickupResponse, {
        'pickup delivery - status 204': r =>
            r.status === 204,
    });

    if (!pickedUp) {
        flowFailure.add(1);
        return;
    }

    // ---------------------------------------------------------
    // 7. START DELIVERY
    // ---------------------------------------------------------

    const startResponse = request(
        'PUT',
        `/api/delivery/deliveries/${deliveryId}/start`,
    );

    const started = check(startResponse, {
        'start delivery - status 204': r =>
            r.status === 204,
    });

    if (!started) {
        flowFailure.add(1);
        return;
    }

    // ---------------------------------------------------------
    // 8. COMPLETE DELIVERY
    // ---------------------------------------------------------

    const completeResponse = request(
        'PUT',
        `/api/delivery/deliveries/${deliveryId}/complete`,
    );

    const completed = check(completeResponse, {
        'complete delivery - status 204': r =>
            r.status === 204,
    });

    if (!completed) {
        flowFailure.add(1);
        return;
    }

    // ---------------------------------------------------------
    // 9. GET DELIVERIES
    // ---------------------------------------------------------

    const deliveriesResponse = request(
        'GET',
        '/api/delivery/deliveries',
    );

    check(deliveriesResponse, {
        'get deliveries - status 200': r =>
            r.status === 200,
    });

    // ---------------------------------------------------------
    // 10. GET PAYMENTS
    // ---------------------------------------------------------

    const paymentsResponse = request(
        'GET',
        '/api/payment/payments',
    );

    check(paymentsResponse, {
        'get payments - status 200': r =>
            r.status === 200,
    });

   
    waitForCompletedDelivery(deliveryId)
   
    sleep(1)
    // ---------------------------------------------------------
    // 11. GET LEDGER
    // ---------------------------------------------------------

    const ledgerResponse = getLedgerEntries();

    if (ledgerResponse.status !== 200) {
        flowFailure.add(1);
        return;
    }

    // ---------------------------------------------------------
    // FLOW COMPLETED
    // ---------------------------------------------------------

    flowSuccess.add(1);

    console.log(
        `Flow completed successfully: ` +
        `delivery=${deliveryId}, ` +
        `payment=${paymentId}`,
    );
}