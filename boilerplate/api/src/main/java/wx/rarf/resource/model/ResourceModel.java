package wx.rarf.resource.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import wx.rarf.resource.entity.Entity;
import wx.rarf.utils.InjectableWorker;

/**
 * Created by apple on 15/12/4.
 */
@EnableTransactionManagement
public class ResourceModel<T extends Entity> {

    @Autowired
    DataSourceTransactionManager transactionManager;

    /**
     * @param injectableWorker
     * @return
     * @function 事务包裹器
     */
    public boolean transactionWrapper(InjectableWorker injectableWorker) {

        //定义默认的事务管理器
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            injectableWorker.doWork();
        } catch (Exception ex) {
            transactionManager.rollback(status);
            return false;
        }

        transactionManager.commit(status);
        return true;

    }

    /**
     * @function 用于动态更新或者生成表名
     */
    public static final class FieldAndValue {

        public String getFieldName() {
            return fieldName;
        }

        public String getValue() {
            return value;
        }

        final String fieldName;
        final String value;

        public FieldAndValue(String fieldName, String value) {
            this.fieldName = fieldName;
            this.value = value;
        }
    }

}
