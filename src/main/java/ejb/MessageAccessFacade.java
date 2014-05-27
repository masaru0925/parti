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
		// TODO: soon: 外出しファイルから。
		// TODO: soon: NativeQueryのNamedQueryでもパフォーマンスが良いのか確認
		public static final String QUERYNAME_NOT_ACCESSED_MESSAGE = "JPQL_NOT_ACCESSED_MESSAGE";
//private static final String JPQL_NOT_ACCESSED_MESSAGE = "select distinct ma.messageId from MessageAccess ma where ma.accessUserAccountId.id <> :accessUserAccountId";
		//private static final String JPQL_NOT_ACCESSED_MESSAGE = " SELECT DISTINCT ma.messageId FROM MessageAccess ma WHERE NOT EXISTS (SELECT u FROM ma.accessUserAccountId u WHERE u.id = :accessUserAccountId)";
		//private static final String JPQL_NOT_ACCESSED_MESSAGE = " SELECT DISTINCT ma.messageId FROM MessageAccess ma EXCEPT SELECT ma.messageId FROM MessageAccess ma WHERE ma.accessUserAccountId.id = :accessUserAccountId";
		private static final String JPQL_NOT_ACCESSED_MESSAGE = " SELECT DISTINCT ma.messageId FROM MessageAccess ma WHERE ma.messageId NOT IN(SELECT ma.messageId FROM MessageAccess ma WHERE ma.accessUserAccountId.id = :accessUserAccountId)";
//		private static final String SQL_NOT_ACCESSED_MESSAGE = " select distinct(A.*) from ( select * from message_access) A  left outer join ( select * from message_access where access_user_account_id = ? ) B on A.message_id = B.message_id where B.message_id is null";

		@Override
		protected EntityManager getEntityManager() {
				return em;
		}

		public MessageAccessFacade() {
				super(MessageAccess.class);
				// TODO: immediate: コーディング
				// TODO: コンストラクタでem直、またはgetEntityManager呼ぶとエラー
//				em.getEntityManagerFactory()
//						.addNamedQuery(
//								QUERYNAME_NOT_ACCESSED_MESSAGE,
//								em.createQuery(
//										JPQL_NOT_ACCESSED_MESSAGE,
//										Message.class)
//						);

		}

		public List<Message> findUnaccessedMessages(Integer accessUserAccountId) {
				Query query = getEntityManager().createQuery(JPQL_NOT_ACCESSED_MESSAGE, Message.class);
				query.setParameter("accessUserAccountId", accessUserAccountId);
				return query.getResultList();
//				return new ArrayList<Message>();
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
