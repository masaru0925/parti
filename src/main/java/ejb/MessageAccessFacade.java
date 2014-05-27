/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import entity.Message;
import entity.MessageAccess;
import entity.UserAccount;
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
public class MessageAccessFacade extends AbstractFacade<MessageAccess> {

		@PersistenceContext(unitName = "parti_parti_war_1.0-SNAPSHOTPU")
		private EntityManager em;

		@Override
		protected EntityManager getEntityManager() {
				return em;
		}

		public MessageAccessFacade() {
				super(MessageAccess.class);
		}

		public void recordAccess(Message message, Integer userId){
						MessageAccess messageAccess;
						messageAccess = new MessageAccess();
						messageAccess.setAccessUserAccountId(new UserAccount(userId));
						messageAccess.setMessageId(message);
						create(messageAccess);
						flush();
		}
}
