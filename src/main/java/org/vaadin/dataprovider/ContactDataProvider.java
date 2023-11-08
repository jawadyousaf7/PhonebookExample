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

    /*
    Keeping CRUD updated using offset
    */
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
    /*
    For Comparing Objects
     */
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
    /*
    getting value of fields
    */
    private static Object valueOf(String fieldName, Contact contact) {
        try {
            Field field = Contact.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(contact);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /*
    Comparing values
     */
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
    /*
    Find record from database
    */
    public Optional<Contact> find(String phoneNumber) {
        return Optional.ofNullable(contactsMap.get(phoneNumber));

    }

    /*
    Checking does record already exists in hashmap
    */
    public boolean contains(String phoneNumber) {
        return contactsMap.containsKey(phoneNumber);
    }

/*
Deleting RECORD
*/
    public void delete(String phoneNumber) {
        contactsMap.remove(phoneNumber);
    }
    /* Checking records from the grid */
    @Override
    protected int sizeInBackEnd(Query<Contact, CrudFilter> query) {
        long count = fetchFromBackEnd(query).count();
        return (int) count;
    }
    /* Persisting record */
    public void persist(Contact contact) {
        contactsMap.put(contact.getPhoneNumber(), contact);

    }

}

