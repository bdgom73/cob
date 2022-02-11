package team.project.Converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Slf4j
public class DateTimeLocalStringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
    @Override
    public LocalDateTime convert(String source) {
        String YYYY_MM_DD_HH_mm = "\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01]) (0[0-9]|1[0-9]|2[0-4]):(0[1-9]|[0-5][0-9])";
        String YYYY_MM_DD = "\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";

        if(source.matches(YYYY_MM_DD_HH_mm)){
            String sourceReplace = source.replaceAll(" ","T");
            log.info("converter ing... {}", sourceReplace);
            return LocalDateTime.parse(sourceReplace);
        }else if( source.matches(YYYY_MM_DD)){
            return LocalDate.parse(source).atTime(0, 0);
        }else{
            return LocalDateTime.parse(source);
        }
    }
}
