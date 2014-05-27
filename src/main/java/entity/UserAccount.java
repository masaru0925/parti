/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author masaru
 */
@Entity
@Table(name = "USER_ACCOUNT")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "UserAccount.findAll", query = "SELECT u FROM UserAccount u"),
		@NamedQuery(name = "UserAccount.findByAddedAt", query = "SELECT u FROM UserAccount u WHERE u.addedAt = :addedAt"),
		@NamedQuery(name = "UserAccount.findByDisabledAt", query = "SELECT u FROM UserAccount u WHERE u.disabledAt = :disabledAt"),
		@NamedQuery(name = "UserAccount.findByModifiedAt", query = "SELECT u FROM UserAccount u WHERE u.modifiedAt = :modifiedAt"),
		@NamedQuery(name = "UserAccount.findById", query = "SELECT u FROM UserAccount u WHERE u.id = :id"),
		@NamedQuery(name = "UserAccount.findByUserName", query = "SELECT u FROM UserAccount u WHERE u.userName = :userName"),
		@NamedQuery(name = "UserAccount.findByPassword", query = "SELECT u FROM UserAccount u WHERE u.password = :password")})
public class UserAccount implements Serializable {
		private static final long serialVersionUID = 1L;
		@Column(name = "ADDED_AT")
        @Temporal(TemporalType.DATE)
		private Date addedAt;
		@Column(name = "DISABLED_AT")
        @Temporal(TemporalType.DATE)
		private Date disabledAt;
		@Column(name = "MODIFIED_AT")
        @Temporal(TemporalType.TIMESTAMP)
		private Date modifiedAt;
		@Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Basic(optional = false)
        @Column(name = "ID")
		private Integer id;
		@Basic(optional = false)
        @NotNull
        @Size(min = 1, max = 128)
        @Column(name = "USER_NAME")
		private String userName;
		@Basic(optional = false)
        @NotNull
        @Size(min = 1, max = 128)
        @Column(name = "PASSWORD")
		private String password;
		@OneToMany(cascade = CascadeType.ALL, mappedBy = "ownerUserAccountId")
		@JsonManagedReference
		private Collection<Parti> partiCollection;
		@OneToMany(cascade = CascadeType.ALL, mappedBy = "postUserAccountId")
		@JsonManagedReference
		private Collection<Message> messageCollection;
		@OneToMany(cascade = CascadeType.ALL, mappedBy = "accessUserAccountId")
		@JsonManagedReference
		private Collection<MessageAccess> messageAccessCollection;

		public UserAccount() {
		}

		public UserAccount(Integer id) {
				this.id = id;
		}

		public UserAccount(Integer id, String userName, String password) {
				this.id = id;
				this.userName = userName;
				this.password = password;
		}

		public Date getAddedAt() {
				return addedAt;
		}

		public void setAddedAt(Date addedAt) {
				this.addedAt = addedAt;
		}

		public Date getDisabledAt() {
				return disabledAt;
		}

		public void setDisabledAt(Date disabledAt) {
				this.disabledAt = disabledAt;
		}

		public Date getModifiedAt() {
				return modifiedAt;
		}

		public void setModifiedAt(Date modifiedAt) {
				this.modifiedAt = modifiedAt;
		}

		public Integer getId() {
				return id;
		}

		public void setId(Integer id) {
				this.id = id;
		}

		public String getUserName() {
				return userName;
		}

		public void setUserName(String userName) {
				this.userName = userName;
		}

		public String getPassword() {
				return password;
		}

		public void setPassword(String password) {
				this.password = password;
		}

		@XmlTransient
		public Collection<Parti> getPartiCollection() {
				return partiCollection;
		}

		public void setPartiCollection(Collection<Parti> partiCollection) {
				this.partiCollection = partiCollection;
		}

		@XmlTransient
		public Collection<Message> getMessageCollection() {
				return messageCollection;
		}

		public void setMessageCollection(Collection<Message> messageCollection) {
				this.messageCollection = messageCollection;
		}

		@XmlTransient
		public Collection<MessageAccess> getMessageAccessCollection() {
				return messageAccessCollection;
		}

		public void setMessageAccessCollection(Collection<MessageAccess> messageAccessCollection) {
				this.messageAccessCollection = messageAccessCollection;
		}

		@Override
		public int hashCode() {
				int hash = 0;
				hash += (id != null ? id.hashCode() : 0);
				return hash;
		}

		@Override
		public boolean equals(Object object) {
				// TODO: Warning - this method won't work in the case the id fields are not set
				if (!(object instanceof UserAccount)) {
						return false;
				}
				UserAccount other = (UserAccount) object;
				if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
						return false;
				}
				return true;
		}

		@Override
		public String toString() {
				return "entity.UserAccount[ id=" + id + " ]";
		}
		
}
