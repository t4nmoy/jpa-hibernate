package persistence.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class EmployeeTypeConverter implements AttributeConverter<EmployeeType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(EmployeeType employeeType) {
        if (employeeType == null) {
            return null;
        }
        return employeeType.getCode();
    }

    @Override
    public EmployeeType convertToEntityAttribute(Integer code) {
        if (code == null){
            return null;
        }
        return Stream.of(EmployeeType.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
