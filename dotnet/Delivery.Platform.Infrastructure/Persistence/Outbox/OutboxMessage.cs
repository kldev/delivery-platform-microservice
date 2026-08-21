namespace Delivery.Platform.Infrastructure.Persistence.Outbox;

public sealed class OutboxMessage
{
    private OutboxMessage()
    {
    }

    public OutboxMessage(
        Guid id,
        Guid aggregateId,
        string module,
        Guid eventId,
        string eventType,
        string payload,
        OutboxMessageStatus status,
        int attempts,
        DateTimeOffset nextAttemptAt,
        DateTimeOffset occurredAt,
        DateTimeOffset createdAt)
    {
        Id = id;
        AggregateId = aggregateId;
        Module = module;
        EventId = eventId;
        EventType = eventType;
        Payload = payload;
        Status = status;
        Attempts = attempts;
        NextAttemptAt = nextAttemptAt;
        OccurredAt = occurredAt;
        CreatedAt = createdAt;
    }

    public Guid Id { get; private set; }

    public Guid AggregateId { get; private set; }

    public string Module { get; private set; } = null!;

    public Guid EventId { get; private set; }

    public string EventType { get; private set; } = null!;

    public string Payload { get; private set; } = null!;

    public OutboxMessageStatus Status { get; private set; }

    public int Attempts { get; private set; }

    public DateTimeOffset NextAttemptAt { get; private set; }

    public DateTimeOffset OccurredAt { get; private set; }

    public DateTimeOffset CreatedAt { get; private set; }

    public DateTimeOffset? PublishedAt { get; private set; }

    public string? LastError { get; private set; }

    public DateTimeOffset? LockedUntil { get; private set; }

    public void MarkPublished(DateTimeOffset publishedAt)
    {
        Status = OutboxMessageStatus.Published;
        PublishedAt = publishedAt;
        LockedUntil = null;
    }

    public void MarkFailed(
        string error,
        int maxAttempts,
        DateTimeOffset nextAttemptAt)
    {
        Attempts++;

        LastError = error;
        NextAttemptAt = nextAttemptAt;
        LockedUntil = null;

        if (Attempts >= maxAttempts)
        {
            Status = OutboxMessageStatus.Dead;
        }
    }

    public void Lock(DateTimeOffset lockedUntil)
    {
        LockedUntil = lockedUntil;
    }
}