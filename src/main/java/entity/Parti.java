/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "PARTI")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Parti.findAll", query = "SELECT p FROM Parti p"),
		@NamedQuery(name = "Parti.findByAddedAt", query = "SELECT p FROM Parti p WHERE p.addedAt = :addedAt"),
		@NamedQuery(name = "Parti.findByDisabledAt", query = "SELECT p FROM Parti p WHERE p.disabledAt = :disabledAt"),
		@NamedQuery(name = "Parti.findByModifiedAt", query = "SELECT p FROM Parti p WHERE p.modifiedAt = :modifiedAt"),
		@NamedQuery(name = "Parti.findById", query = "SELECT p FROM Parti p WHERE p.id = :id"),
		@NamedQuery(name = "Parti.findByName", query = "SELECT p FROM Parti p WHERE p.name = :name")})
public class Parti implements Serializable {
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
        @Column(name = "NAME")
		private String name;
		@JoinColumn(name = "OWNER_USER_ACCOUNT_ID", referencedColumnName = "ID")
        @ManyToOne(optional = false)
		@JsonBackReference
		private UserAccount ownerUserAccountId;
		@OneToMany(cascade = CascadeType.ALL, mappedBy = "partyId")
		@JsonManagedReference
		private Collection<Message> messageCollection;

		public Parti() {
		}

		public Parti(Integer id) {
				this.id = id;
		}

		public Parti(Integer id, String name) {
				this.id = id;
				this.name = name;
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

		public String getName() {
				return name;
		}

		public void setName(String name) {
				this.name = name;
		}

		public UserAccount getOwnerUserAccountId() {
				return ownerUserAccountId;
		}

		public void setOwnerUserAccountId(UserAccount ownerUserAccountId) {
				this.ownerUserAccountId = ownerUserAccountId;
		}

		@XmlTransient
		public Collection<Message> getMessageCollection() {
				return messageCollection;
		}

		public void setMessageCollection(Collection<Message> messageCollection) {
				this.messageCollection = messageCollection;
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
				if (!(object instanceof Parti)) {
						return false;
				}
				Parti other = (Parti) object;
				if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
						return false;
				}
				return true;
		}

		@Override
		public String toString() {
				return "entity.Parti[ id=" + id + " ]";
		}
		
}
