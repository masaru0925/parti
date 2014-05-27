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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author masaru
 */
@Entity
@Table(name = "MESSAGE")
@XmlRootElement
@NamedQueries({
/**@code-modify*/	@NamedQuery(name="findNotAccessedMessages", query=" SELECT DISTINCT ma.messageId FROM MessageAccess ma WHERE ma.messageId NOT IN ( SELECT ma.messageId FROM MessageAccess ma WHERE ma.accessUserAccountId.id = :accessUserAccountId) "),
/**@code-modify*/
		@NamedQuery(name = "Message.findAll", query = "SELECT m FROM Message m"),
		@NamedQuery(name = "Message.findByAddedAt", query = "SELECT m FROM Message m WHERE m.addedAt = :addedAt"),
		@NamedQuery(name = "Message.findByDisabledAt", query = "SELECT m FROM Message m WHERE m.disabledAt = :disabledAt"),
		@NamedQuery(name = "Message.findByModifiedAt", query = "SELECT m FROM Message m WHERE m.modifiedAt = :modifiedAt"),
		@NamedQuery(name = "Message.findById", query = "SELECT m FROM Message m WHERE m.id = :id"),
		@NamedQuery(name = "Message.findByBody", query = "SELECT m FROM Message m WHERE m.body = :body")})
public class Message implements Serializable {
/**@code-modify*/public static final String QUERY_findNotAccessedMessages = "findNotAccessedMessages";
/**@code-modify*/
/**@code-modify*/public static final String PARAM_findNotAccessedMessages_accessUserAccountId = "accessUserAccountId";
/**@code-modify*/
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
		@Size(max = 512)
        @Column(name = "BODY")
		private String body;
		@JoinColumn(name = "PARTY_ID", referencedColumnName = "ID")
/**@code-modify*/@JsonBackReference
        @ManyToOne(optional = false)
		private Parti partyId;
		@JoinColumn(name = "POST_USER_ACCOUNT_ID", referencedColumnName = "ID")
/**@code-modify*/@JsonBackReference
        @ManyToOne(optional = false)
		private UserAccount postUserAccountId;
/**@code-modify*/@JsonManagedReference
		@OneToMany(cascade = CascadeType.ALL, mappedBy = "messageId")
		private Collection<MessageAccess> messageAccessCollection;

		public Message() {
		}

		public Message(Integer id) {
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

		public String getBody() {
				return body;
		}

		public void setBody(String body) {
				this.body = body;
		}

		public Parti getPartyId() {
				return partyId;
		}

		public void setPartyId(Parti partyId) {
				this.partyId = partyId;
		}

		public UserAccount getPostUserAccountId() {
				return postUserAccountId;
		}

		public void setPostUserAccountId(UserAccount postUserAccountId) {
				this.postUserAccountId = postUserAccountId;
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
				if (!(object instanceof Message)) {
						return false;
				}
				Message other = (Message) object;
				if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
						return false;
				}
				return true;
		}

		@Override
		public String toString() {
				return "entity.Message[ id=" + id + " ]";
		}
		
}
