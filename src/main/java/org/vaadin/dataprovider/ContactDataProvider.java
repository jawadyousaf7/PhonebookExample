package org.vaadin.dataprovider;

import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import org.vaadin.entity.Contact;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ContactDataProvider extends AbstractBackEndDataProvider<Contact, CrudFilter> {

    private static Map<String, Contact> contactsMap = new HashMap<>();
    Contact contact = new Contact();

    public static void setContactMap(Map<String, Contact> map) {
        contactsMap = map;
    }

    @Override
    protected Stream<Contact> fetchFromBackEnd(Query<Contact, CrudFilter> query) {
        int offset = query.getOffset();
        int limit = query.getLimit();
        Stream<Contact> stream = contactsMap.values().stream();
        if (query.getFilter().isPresent()) {
            stream = stream.filter(predicate(query.getFilter().get())).sorted(comparator(query.getFilter().get()));
        }
        return stream.skip(offset).limit(limit);
    }

    private static Predicate<Contact> predicate(CrudFilter filter) {
        return filter.getConstraints().entrySet().stream().map(constraint -> (Predicate<Contact>) contact -> {
            try {
                Object value = valueOf(constraint.getKey(), contact);
                return value != null && value.toString().toLowerCase().contains(constraint.getValue().toLowerCase());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }).reduce(Predicate::and).orElse(e -> true);
    }

    private static Object valueOf(String fieldName, Contact contact) {
        try {
            Field field = Contact.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(contact);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Comparator<Contact> comparator(CrudFilter filter) {
        return filter.getSortOrders().entrySet().stream().map(sortClause -> {
            try {
                Comparator<Contact> comparator = Comparator.comparing(contact -> (Comparable) valueOf(sortClause.getKey(), contact));

                if (sortClause.getValue() == SortDirection.DESCENDING) {
                    comparator = comparator.reversed();
                }
                return comparator;

            } catch (Exception ex) {
                return (Comparator<Contact>) (o1, o2) -> 0;
            }
        }).reduce(Comparator::thenComparing).orElse((o1, o2) -> 0);
    }

    public Optional<Contact> find(String phoneNumber) {
        return Optional.ofNullable(contactsMap.get(phoneNumber));

    }


    public boolean contains(String phoneNumber) {
        return contactsMap.containsKey(phoneNumber);
    }


    public void delete(String phoneNumber) {
        contactsMap.remove(phoneNumber);
    }

    @Override
    protected int sizeInBackEnd(Query<Contact, CrudFilter> query) {
        long count = fetchFromBackEnd(query).count();
        return (int) count;
    }
    public Map<String, Contact> getContacts() {

        contact.setPhoneNumber("92333666997894");
        contact.setName("Ali");
        contact.setEmail("abc@xyz.com");
        contact.setStreet("6");
        contact.setCity("Lahore");
        contact.setCountry("Pakistan");
        contactsMap.put(contact.getPhoneNumber(), contact);

        return contactsMap;
    }

    public Map<String, Contact> addContact(Contact contact) {
        contactsMap.put(contact.getPhoneNumber() ,contact);
        return contactsMap;
    }

    public void deleteContact(String phoneNumber) {
        contactsMap.remove(phoneNumber);
    }

}

