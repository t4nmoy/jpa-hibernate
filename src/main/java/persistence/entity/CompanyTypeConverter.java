package persistence.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class CompanyTypeConverter implements AttributeConverter<CompanyType, String> {

    @Override
    public String convertToDatabaseColumn(CompanyType companyType) {
        if (companyType == null) {
            return null;
        }
        return companyType.getCode();
    }

    @Override
    public CompanyType convertToEntityAttribute(String code) {
        if (code == null){
            return null;
        }
        return Stream.of(CompanyType.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
