package persistence.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class Customer extends AuditableEntity {

    @NonNull
    @NotBlank(message = "customer name is required")
    private String name;

    @NonNull
    @NotNull(message = "company is required")
    @ManyToOne
    private Company company;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, Integer> itemQuantityMap = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<PhoneNumber> phones = new HashSet<>();

    public Map<String, Integer> getItemQuantityMap() {
        return Collections.unmodifiableMap(itemQuantityMap);
    }

    public void addItemQuantity(String itemName, Integer quantity) {
        if (!this.itemQuantityMap.containsKey(itemName)) {
            this.itemQuantityMap.put(itemName, quantity);
        }
    }

    public void removeItemQuantity(String itemName) {
        if (this.itemQuantityMap.containsKey(itemName)) {
            this.itemQuantityMap.remove(itemName);
        }
    }

    public Set<PhoneNumber> getPhones() {
        return Collections.unmodifiableSet(phones);
    }

    public void addPhone(PhoneNumber phone) {
        if (!this.phones.contains(phone)) {
            this.phones.add(phone);
        }
    }
}
