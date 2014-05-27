/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import entity.MessageAccess;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

}
