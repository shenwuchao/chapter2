package org.smart4j.chapter2.model;

import java.util.Objects;

public class Customer {
    /*
    * ID
    * */
    private long id;
    /*
    * 客户姓名
    * */
    private String name;
    /*
    * 联系人
    * */
    private String contact;
    /*
    * 电话号码
    * */
    private String telephone;
    /*
    * 邮箱地址
    * */
    private String email;
    /*
    * 备注
    * */
    private String remark;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

    public String getRemark() {
        return remark;
    }

    public void setId(long id) {

        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    /*
    * 重写toString
    * */

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", contact='" + contact + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id == customer.id &&
                Objects.equals(name, customer.name) &&
                Objects.equals(contact, customer.contact) &&
                Objects.equals(telephone, customer.telephone) &&
                Objects.equals(email, customer.email) &&
                Objects.equals(remark, customer.remark);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, contact, telephone, email, remark);
    }
}
