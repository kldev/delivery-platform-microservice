using Delivery.Platform.PaymentService.Events.Contracts;

namespace Delivery.Platform.PaymentService.Publisher;

public static class EventToTopic
{
    public static string Map(string eventType, string module)
    {
        List<string> ownTopics = [PaymentEventNames.Paid, PaymentEventNames.Declined];
        return ownTopics.Contains(eventType) ? eventType : $"{module}.events";
    }
}