package zhangchongantest.neu.edu.newtest.Database;

import android.content.Context;

import java.util.List;

import zhangchongantest.neu.edu.newtest.Contract;

public class DatabasePresenter implements Contract.IPresenter{
    private Contract.IView mCallBack;
    private Context mContext;
    private DatabaseModel dbModel;
    private static final String dbName = "TEST_GREEN_DAO";

    public DatabasePresenter(Context context,Contract.IView callBack) {
        mContext = context;
        mCallBack = callBack;
        dbModel = new DatabaseModel(mContext,this,dbName);
    }

//    public DaoSession getDaoSession(){
//        if (dbModel!=null){
//            return dbModel.getDaoSession();
//        }
//        return null;
//    }

    public void insertStudentDao(StudentBean studentBean){
       dbModel.insertStudentDao(studentBean);
       // mCallBack.DbPresenterResult(row);
    }

    public void insertStudentDao(List<StudentBean> mStudentList) {
        dbModel.insertStudentDao(mStudentList);
       // mCallBack.DbPresenterResult(str_insert_success);
    }

    public void queryStudentDao(){
        dbModel.queryStudentDao();
       // mCallBack.DbPresenterResultList(list);
    }

    public void queryStudetnDao(String whereStudentId){
       dbModel.queryStudentDao(whereStudentId);
        //mCallBack.DbPresenterResultList(list);
    }

    public void getStudetnDaoCount(){
        dbModel.getStudetnDaoCount();
       // mCallBack.DbPresenterResult(count);
    }

    public void updateStudentDao(StudentBean studentBean){
        dbModel.updateStudentDao(studentBean);
       // mCallBack.DbPresenterResult(str_update_success);
    }

    public void updateStudentDao(List<StudentBean> mStudentList){
        dbModel.updateStudentDao(mStudentList);
        //mCallBack.DbPresenterResult(str_update_success);
    }

    public void deleteStudentDao(StudentBean studentBean){
        dbModel.deleteStudentDao(studentBean);
       // mCallBack.DbPresenterResult(str_delete_success);
    }

    public void deleteStudentDao(List<StudentBean> mStudentList){
        dbModel.deleteStudentDao(mStudentList);
    }

    @Override
    public void DbModelerResult(Object o) {
        mCallBack.DbPresenterResult(o);
    }

    @Override
    public void DbModelResultList(List list) {
        mCallBack.DbPresenterResultList(list);
    }


    public interface DbPresenterCallback<T>{
        void DbPresenterResult(T t);
        void DbPresenterResultList(List<? extends BaseBean> list);
    }
}
