package persistence.entity;


import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = "long"))
@Filter(name = "tenantFilter", condition = "tenant_id =:tenantId")
@Table(name = "customer")
public class Customer extends TenantEntityBase {

    @NonNull
    @NotBlank(message = "customer name is required")
    @Column(nullable = false)
    private String name;

    @Setter
    private Long number;

    @NonNull
    @Setter
    @NotNull(message = "customer type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CustomerType type;

    @NonNull
    @NotNull(message = "company is required")
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="customer_item_quantity_map", joinColumns =  @JoinColumn(name = "customer_id"))
    @MapKeyColumn(name="item_name")
    @Column(name = "item_quantity")
    private Map<String, Integer> itemQuantityMap = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "customer_phones", joinColumns = @JoinColumn(name = "customer_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "number", column = @Column(name = "phone_number")),
            @AttributeOverride(name = "type", column = @Column(name = "number_type"))
    })
    private Set<PhoneNumber> phones = new HashSet<>();

    @Setter
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true)
    @JoinColumn(name = "customer_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Contact> contacts = new ArrayList<>();

    public Map<String, Integer> getItemQuantityMap() {
        return Collections.unmodifiableMap(itemQuantityMap);
    }

    public void addItemQuantity(String itemName, Integer quantity) {
        if (this.itemQuantityMap.containsKey(itemName)) {
            return;
        }
        this.itemQuantityMap.put(itemName, quantity);
    }

    public void removeItemQuantity(String itemName) {
        this.itemQuantityMap.remove(itemName);
    }

    public Set<PhoneNumber> getPhones() {
        return Collections.unmodifiableSet(phones);
    }

    public void addPhone(PhoneNumber phone) {
        this.phones.add(phone);
    }
}
