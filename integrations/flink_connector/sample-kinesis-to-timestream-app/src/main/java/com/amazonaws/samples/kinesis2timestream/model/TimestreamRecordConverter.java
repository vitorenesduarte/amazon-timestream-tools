package com.amazonaws.samples.kinesis2timestream.model;

import java.util.List;

import software.amazon.awssdk.services.timestreamwrite.model.Dimension;
import software.amazon.awssdk.services.timestreamwrite.model.MeasureValue;
import software.amazon.awssdk.services.timestreamwrite.model.MeasureValueType;
import software.amazon.awssdk.services.timestreamwrite.model.Record;
import software.amazon.awssdk.services.timestreamwrite.model.TimeUnit;

public class TimestreamRecordConverter {
    public static Record convert(final KinesisRecord event) {
        List<Dimension> dimensions = List.of(
                Dimension.builder()
                        .name("table_name")
                        .value(event.getTableName()).build(),
                Dimension.builder()
                        .name("event_type")
                        .value(event.getEventType()).build()
        );

        // TODO: unclear whether user should be a measure or a dimension
        List<MeasureValue> measureValues = List.of(
                MeasureValue.builder()
                        .name("user")
                        .type(MeasureValueType.VARCHAR)
                        .value(event.getUser()).build()
        );

        // TODO: only generate record if user non-empty
        return Record.builder()
                .dimensions(dimensions)
                .measureName("events_record")
                .measureValueType("MULTI")
                .measureValues(measureValues)
                .timeUnit(TimeUnit.SECONDS)
                .time(Long.toString(event.getCreatedAt())).build();
    }
}
