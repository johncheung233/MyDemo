package zhangchongantest.neu.edu.newtest.Database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class StudentBean extends BaseBean{
    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "student_id") @NotNull
    private long studentId;

    @Property(nameInDb = "student_name") @NotNull
    private String studentName;

    @Generated(hash = 663312927)
    public StudentBean(Long id, long studentId, @NotNull String studentName) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
    }

    @Generated(hash = 2097171990)
    public StudentBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getStudentId() {
        return this.studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

 


}
