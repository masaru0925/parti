/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ejb;

import entity.Message;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author masaru
 */
@Stateless
public class MessageFacade extends AbstractFacade<Message> {
/**@code-modify*/public List<Message> findNotAccessedMessages(Integer accessUserAccountId){
/**@code-modify*/	Query query = getEntityManager().createNamedQuery(Message.QUERY_findNotAccessedMessages, Message.class);
/**@code-modify*/	query.setParameter(Message.PARAM_findNotAccessedMessages_accessUserAccountId, accessUserAccountId);
/**@code-modify*/	return query.getResultList();
/**@code-modify*/}
/**@code-modify*/
/**@code-modify*/
		@PersistenceContext(unitName = "parti_parti_war_1.0-SNAPSHOTPU")
		private EntityManager em;

		@Override
		protected EntityManager getEntityManager() {
				return em;
		}

		public MessageFacade() {
				super(Message.class);
		}
		
}
