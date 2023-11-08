package org.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.router.Route;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import org.vaadin.dataprovider.ContactDataProvider;
import org.vaadin.entity.Contact;
import org.vaadin.dao.ContactsDao;



@SuppressWarnings("serial")
@Route("")
public class PhoneBookView extends VerticalLayout {

    private Crud<Contact> crud;
    private ContactDataProvider contactDataProvider = new ContactDataProvider();
    private Binder<Contact> binder;
    private TextField name = new TextField("Name");
    private TextField phoneNumber = new TextField("Phone number");
    private TextField email = new TextField("email");
    private TextField street = new TextField("Street");
    private TextField city = new TextField("City");
    private TextField country =  new TextField("Country");
    private String editPhoneNumber;
    private Boolean isAddContactClicked = false;
     static Map<String, Contact> contactList = new HashMap<String, Contact>();
    private LocalDateTime lastUpdatedTimeFlag;
    private transient ContactsDao contactsDao = new ContactsDao();
    private boolean warnOnAlreadyUpdatedContact = true;


    public PhoneBookView() {
        initView();
    }

    /*
    Main method
     */
    private void initView() {
        crud = new Crud<>(Contact.class, createEditor());
        crud.setEditOnClick(true);
        crud.setSizeFull();
        setGridColumns();
        ContactDataProvider.setContactMap(contactsDao.getContacts());
        crud.setDataProvider(contactDataProvider);
        crud.addSaveListener(e -> {
            saveContact(e.getItem());
        });
        crud.addEditListener(e -> {
            editPhoneNumber = e.getItem().getPhoneNumber();
            isAddContactClicked = false;
            lastUpdatedTimeFlag = e.getItem().getLastUpdatedTime();
            validateFields();

        });
        crud.addDeleteListener(e -> deleteContact(e.getItem().getPhoneNumber()));
        crud.setNewButton(customAddContactButton());
        add(crud);
    }
    /* Creating add button*/
    private Button customAddContactButton() {
        Button addContactButton = new Button("Add Contact", VaadinIcon.PLUS.create());
        addContactButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        addContactButton.addClickListener(e -> {
            isAddContactClicked = true;
            editPhoneNumber = null;
            crud.edit(new Contact(), Crud.EditMode.NEW_ITEM);
            validateFields();
        });
        return addContactButton;
    }
    /*
    Creating Editor
    */
    private CrudEditor<Contact> createEditor() {

        FormLayout formLayout = new FormLayout(name, phoneNumber, email, street, city, country);
        binder = new Binder<>(Contact.class);
        binder.forField(name).asRequired().bind(Contact::getName, Contact::setName);
        binder.forField(phoneNumber).asRequired().bind(Contact::getPhoneNumber, Contact::setPhoneNumber);
        binder.forField(email).asRequired().bind(Contact::getEmail, Contact::setEmail);
        binder.bind(street, Contact::getStreet, Contact::setStreet);
        binder.bind(city, Contact::getCity, Contact::setCity);
        binder.bind(country, Contact::getCountry, Contact::setCountry);
        return new BinderCrudEditor<>(binder, formLayout);
    }
    /* Save Contact */
    private void saveContact(Contact contact) {

        if (!isAddContactClicked && warnOnAlreadyUpdatedContact && isContactAlreadyUpdated()){
            createWarningDialogue();
            crud.getDataProvider().refreshItem(contact);

        }
        contact.setLastUpdatedTime(LocalDateTime.now());
        boolean isSuccess = true;

        if (Objects.isNull(editPhoneNumber)) {
            isSuccess = contactsDao.addContact(contact);
        } else if (!contact.getPhoneNumber().equals(editPhoneNumber)) {
            deleteContact(editPhoneNumber);
            isSuccess = contactsDao.addContact(contact);
        } else {
            isSuccess = contactsDao.updateContact(contact);
        }
        if (isSuccess) {
            contactDataProvider.persist(contact);
        }
    }
    /* Deleting Contact */
    private void deleteContact(String phoneNumber) {

        if (contactsDao.deleteContact(phoneNumber)) {
            contactDataProvider.delete(phoneNumber);
        }
    }
    /* Adding columns */
    private void setGridColumns() {
        crud.getGrid().removeColumn(crud.getGrid().getColumnByKey("lastUpdatedTime"));
        crud.getGrid().setColumnOrder(crud.getGrid().getColumnByKey("name"), crud.getGrid().getColumnByKey("phoneNumber"), crud.getGrid().getColumnByKey("email"), crud.getGrid().getColumnByKey("street"), crud.getGrid().getColumnByKey("city"), crud.getGrid().getColumnByKey("country"));
    }
    /* Basic form Validations */
    private void validateFields() {

        SerializablePredicate<String> alreadyExist = value -> !(contactDataProvider.contains(phoneNumber.getValue()) && (isAddContactClicked || !phoneNumber.getValue().equals(editPhoneNumber)));
        Binder.Binding<Contact, String> phoneBinding = binder.forField(phoneNumber).asRequired().withValidator(alreadyExist, "Phone Number Already Exist").withValidator(new RegexpValidator("Only 0-9 allowed","\\d*"))
                .bind(Contact::getPhoneNumber, Contact::setPhoneNumber);
        phoneNumber.addValueChangeListener(e -> phoneBinding.validate());
        binder.forField(email).asRequired().withValidator(new EmailValidator("Invalid Email Address")).bind(Contact::getEmail, Contact::setEmail);

        Binder.Binding<Contact, String> nameBinding = binder.forField(name).withValidator(name -> {
            if ( Pattern.matches("[a-zA-Z]+",name)) {
                return true;
            }
            return false;
        }, "Only letters required").bind(Contact::getName, Contact::setName);

        Binder.Binding<Contact, String> cityBinging = binder.forField(city).withValidator(city -> {
            if ( Pattern.matches("[a-zA-Z]+",city)) {
                return true;
            }
            return false;
        }, "Only letters required").bind(Contact::getCity, Contact::setCity);

        Binder.Binding<Contact, String> countryBinding = binder.forField(country).withValidator(country -> {
            if ( Pattern.matches("[a-zA-Z]+",country)) {
                return true;
            }
            return false;
        }, "Only letters required").bind(Contact::getCountry, Contact::setCountry);

    }
    /* Creating waring dialogue */
    private void createWarningDialogue() {
        ConfirmDialog warningOnAlreadyUpdateContact = new ConfirmDialog();
        warningOnAlreadyUpdateContact.setHeader("Already Updated");
        warningOnAlreadyUpdateContact.setText("This Contact is already updated by another user.");
        warningOnAlreadyUpdateContact.setConfirmText("OK");
        warningOnAlreadyUpdateContact.open();
        warnOnAlreadyUpdatedContact = false;

    }

    /* Comparing last Updated time with current record in edit mode */
    private Boolean isContactAlreadyUpdated() {
        Optional<Contact> contactOptional = contactDataProvider.find(editPhoneNumber);
        if (contactOptional.isPresent() && contactOptional.get().getLastUpdatedTime().equals(lastUpdatedTimeFlag))
            return false;
        return true;
    }


}
