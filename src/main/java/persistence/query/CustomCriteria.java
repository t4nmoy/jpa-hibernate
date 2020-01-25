package persistence.query;

public class CustomCriteria {
    private String key;
    private Object value;
    private QueryOperation operation;

    public CustomCriteria(String key, Object value, QueryOperation operation) {
        this.key = key;
        this.value = value;
        this.operation = operation;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public QueryOperation getOperation() {
        return operation;
    }
}
