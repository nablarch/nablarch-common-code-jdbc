package nablarch.common.code;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * コードパターン
 * 
 */
@Entity
@Table(name = "CODE_PATTERN")
public class CodePattern {
    
    public CodePattern() {
    }
    
    public CodePattern(String id, String value, String pattern1, String pattern2, String pattern3) {
        this.id = id;
        this.value = value;
        this.pattern1 = pattern1;
        this.pattern2 = pattern2;
        this.pattern3 = pattern3;
    }

    @Id
    @Column(name = "CODE_ID01", length = 4, nullable = false)
    public String id;
    
    @Id
    @Column(name = "VALUE", length = 10, nullable = false)
    public String value;
    
    @Column(name = "PATTERN1", length = 1, nullable = false)
    public String pattern1;
    
    @Column(name = "PATTERN2", length = 1, nullable = false)
    public String pattern2;
    
    @Column(name = "PATTERN3", length = 1, nullable = false)
    public String pattern3;
}