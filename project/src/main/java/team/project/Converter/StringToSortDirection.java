package team.project.Converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
public class StringToSortDirection implements Converter<String, Sort.Direction> {
    @Override
    public Sort.Direction convert(String source) {
        String val = source.toUpperCase();
        if(val.equals("DESC") || val.equals("ASC") ){
            return Sort.Direction.valueOf(val);
        }else{
            return Sort.Direction.DESC;
        }
    }
}
