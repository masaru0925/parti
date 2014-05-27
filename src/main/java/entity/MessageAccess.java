/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author masaru
 */
@Entity
@Table(name = "MESSAGE_ACCESS")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "MessageAccess.findAll", query = "SELECT m FROM MessageAccess m"),
		@NamedQuery(name = "MessageAccess.findByAddedAt", query = "SELECT m FROM MessageAccess m WHERE m.addedAt = :addedAt"),
		@NamedQuery(name = "MessageAccess.findByDisabledAt", query = "SELECT m FROM MessageAccess m WHERE m.disabledAt = :disabledAt"),
		@NamedQuery(name = "MessageAccess.findByModifiedAt", query = "SELECT m FROM MessageAccess m WHERE m.modifiedAt = :modifiedAt"),
		@NamedQuery(name = "MessageAccess.findById", query = "SELECT m FROM MessageAccess m WHERE m.id = :id")})
public class MessageAccess implements Serializable {
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
		@JoinColumn(name = "ACCESS_USER_ACCOUNT_ID", referencedColumnName = "ID")
        @ManyToOne(optional = false)
		@JsonBackReference
		private UserAccount accessUserAccountId;
		@JoinColumn(name = "MESSAGE_ID", referencedColumnName = "ID")
        @ManyToOne(optional = false)
		@JsonBackReference
		private Message messageId;

		public MessageAccess() {
		}

		public MessageAccess(Integer id) {
				this.id = id;
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

		public UserAccount getAccessUserAccountId() {
				return accessUserAccountId;
		}

		public void setAccessUserAccountId(UserAccount accessUserAccountId) {
				this.accessUserAccountId = accessUserAccountId;
		}

		public Message getMessageId() {
				return messageId;
		}

		public void setMessageId(Message messageId) {
				this.messageId = messageId;
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
				if (!(object instanceof MessageAccess)) {
						return false;
				}
				MessageAccess other = (MessageAccess) object;
				if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
						return false;
				}
				return true;
		}

		@Override
		public String toString() {
				return "entity.MessageAccess[ id=" + id + " ]";
		}
		
}
