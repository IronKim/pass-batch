package com.fs.pass.repository.notification;

import com.fs.pass.repository.booking.BookingEntity;
import com.fs.pass.util.LocalDateTimeUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationModelMapper {

    NotificationModelMapper INSTANCE = Mappers.getMapper(NotificationModelMapper.class);

    @Mapping(target = "uuid", source = "bookingEntity.userEntity.uuid")
    @Mapping(target = "text", source = "bookingEntity.startedAt", qualifiedByName = "text")
    NotificationEntity toNotificationEntity(BookingEntity bookingEntity, NotificationEvent event);

    // 알람 보낼 메세지 생성
    @Named("text")
    default String text(LocalDateTime startedAt) {
        return String.format("안녕하세요. %s 수업을 시작합니다. 수업 전 출석 체크 부탁드립니다. \uD83D\uDE0A", LocalDateTimeUtils.format(startedAt)); // \uD83D\uDE0A: 웃는 얼굴 이모티콘
    }

}
