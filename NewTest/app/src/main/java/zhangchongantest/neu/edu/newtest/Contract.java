package zhangchongantest.neu.edu.newtest;

import java.util.List;

import zhangchongantest.neu.edu.newtest.Database.BaseBean;
import zhangchongantest.neu.edu.newtest.Database.StudentBean;

public class Contract {
    public interface IView<T> {
        void DbPresenterResult(T t);
        void DbPresenterResultList(List<? extends BaseBean> list);
    }

    public interface IPresenter<T> {
        void DbModelerResult(T t);
        void DbModelResultList(List<? extends BaseBean> list);
    }

    public interface IModel {

    }
}
