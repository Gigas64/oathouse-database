/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * @(#)ObjectBeanTestEnum.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.objectstore;

/**
 * The {@code ObjectBeanTestEnum} Enumeration
 *
 * @author Darryl Oatridge
 * @version 1.00 22-Jan-2011
 */
public enum ObjectBeanTestEnum {
//    // concessions
//    HolidayConcessionBean("com.oathouse.tse.application.concessions.HolidayConcessionBean",false),
//    MilkConcessionBean("com.oathouse.tse.application.concessions.MilkConcessionBean",false),
//    // properties
//    AccountHolderBean("com.oathouse.tse.application.properties.AccountHolderBean",false),
//    SystemPropertiesBean("com.oathouse.tse.application.properties.SystemPropertiesBean",false),
//    // accounts
//    FixedAdjustmentBean("com.oathouse.tse.booking.accounts.invoice.FixedAdjustmentBean",false),
//    PeriodSdValueBean("com.oathouse.tse.booking.accounts.invoice.PeriodSdValueBean",false),
//    BookingChargeBean("com.oathouse.tse.booking.accounts.invoice.BookingChargeBean",false),
//    FixedChargeBean("com.oathouse.tse.booking.accounts.invoice.FixedChargeBean",false),
//    InvoiceBean("com.oathouse.tse.booking.accounts.invoice.InvoiceBean",false),
//    InvoiceCreditBean("com.oathouse.tse.booking.accounts.transaction.InvoiceCreditBean",false),
//    CustomerReceiptBean("com.oathouse.tse.booking.accounts.transaction.CustomerReceiptBean",false),
//    CustomerCreditBean("com.oathouse.tse.booking.accounts.transaction.CustomerCreditBean",false),
//    PaymentBean("com.oathouse.tse.booking.accounts.transaction.PaymentBean",false),
//    //bookings
//    AgeStartBean("com.oathouse.tse.booking.bookings.AgeStartBean",false),
//    BookingBean("com.oathouse.tse.booking.bookings.BookingBean",false),
//    BookingHistoryBean("com.oathouse.tse.booking.bookings.BookingHistoryBean",false),
//    BookingRequestBean("com.oathouse.tse.booking.bookings.BookingRequestBean",false),
//    //config
//    AgeRangeBean("com.oathouse.tse.booking.config.AgeRangeBean",false),
//    ChildRoomStartBean("com.oathouse.tse.booking.config.ChildRoomStartBean",false),
//    DayRangeBean("com.oathouse.tse.booking.config.DayRangeBean",false),
//    RoomConfigBean("com.oathouse.tse.booking.config.RoomConfigBean",false),
//    TimetableBean("com.oathouse.tse.booking.config.TimetableBean",false),
//    ChildEducationTimetableBean("com.oathouse.tse.booking.config.education.ChildEducationTimetableBean",false),
//    // profile
//    AccountBean("com.oathouse.tse.booking.profile.AccountBean",false),
//    ChildBean("com.oathouse.tse.booking.profile.ChildBean",false),
//    ContactBean("com.oathouse.tse.booking.profile.ContactBean",false),
//    MedicalBean("com.oathouse.tse.booking.profile.MedicalBean",false),
//    RelationBean("com.oathouse.tse.booking.profile.RelationBean",false),
    // example
    ExampleBean("com.oathouse.oss.storage.objectstore.example.ExampleBean",false),
    ExampleInheritBean("com.oathouse.oss.storage.objectstore.example.ExampleInheritBean",false);

    private String cls;
    private boolean printXml;

    private ObjectBeanTestEnum(String cls, boolean printXml) {
        this.cls = cls;
        this.printXml = printXml;
    }

    public String getCls() {
        return cls;
    }

    public boolean isPrintXml() {
        return printXml;
    }
}
