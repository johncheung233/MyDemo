package zhangchongantest.neu.edu.newtest.Database;

import android.content.Context;
import android.util.Log;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import zhangchongantest.neu.edu.newtest.Contract;

public class DatabaseModel {
    private DaoSession daoSession;
    private Contract.IPresenter modelCallback;

    private final String str_insert_success = "INSERT_SUCCESS";
    private final String str_update_success = "UPDATE_SUCCESS";
    private final String str_delete_success = "DELETE_SUCCESS";

    public DatabaseModel(Context context,Contract.IPresenter callback,String dbName) {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context,dbName);
        daoSession = new DaoMaster(devOpenHelper.getWritableDb()).newSession();
        modelCallback  =callback;
        //使用 加密数据库
        //  Database encryptedReadableDb = devOpenHelper.getEncryptedReadableDb("123456");
        // daoSession =   new DaoMaster(encryptedReadableDb).newSession();
    }

    protected DaoSession getDaoSession() {
        return daoSession;
    }

    protected void insertStudentDao(StudentBean studentBean){
        StudentBeanDao studentDao = daoSession.getStudentBeanDao();
        Long row = studentDao.insertOrReplace(studentBean);
        //          .insert();row
        modelCallback.DbModelerResult(row);
    }

    protected void insertStudentDao(List<StudentBean> mStudentList) {
        StudentBeanDao studentDao = daoSession.getStudentBeanDao();
        studentDao.insertInTx(mStudentList);
        //          .insertOrReplaceInTx();
        modelCallback.DbModelerResult(str_insert_success);
    }

    protected void queryStudentDao(){
        StudentBeanDao studentDao = daoSession.getStudentBeanDao();
        List<StudentBean> list = studentDao.loadAll();
        //studentDao.queryBuilder().list();
        modelCallback.DbModelResultList(list);
    }

    protected void queryStudentDao(String whereStudentId){
        StudentBeanDao studentDao = daoSession.getStudentBeanDao();
        QueryBuilder queryBuilder = studentDao.queryBuilder();
        queryBuilder.where(StudentBeanDao.Properties.StudentId.eq(whereStudentId))
                .orderAsc(StudentBeanDao.Properties.Id);
        List<StudentBean> list =  queryBuilder.list();
        modelCallback.DbModelResultList(list);
    }

    protected void getStudetnDaoCount(){
        StudentBeanDao studentDao = daoSession.getStudentBeanDao();
        Long count =  studentDao.count();
        modelCallback.DbModelerResult(count);
    }

    protected void updateStudentDao(StudentBean studentBean){
        StudentBeanDao studentDao = daoSession.getStudentBeanDao();
        studentDao.update(studentBean);
        modelCallback.DbModelerResult(str_update_success);
    }

    protected void updateStudentDao(List<StudentBean> mStudentList){
        StudentBeanDao studentDao = daoSession.getStudentBeanDao();
        studentDao.updateInTx(mStudentList);
        modelCallback.DbModelerResult(str_update_success);
    }

    protected void deleteStudentDao(StudentBean studentBean){
        StudentBeanDao studentDao = daoSession.getStudentBeanDao();
        studentDao.delete(studentBean);
        modelCallback.DbModelerResult(str_delete_success);
    }

    protected void deleteStudentDao(List<StudentBean> mStudentList){
        StudentBeanDao studentDao = daoSession.getStudentBeanDao();
        studentDao.deleteInTx(mStudentList);
        modelCallback.DbModelerResult(str_delete_success);
    }

}
