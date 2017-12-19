package nablarch.common.code;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * コード名称
 * 
 */
@Entity
@Table(name = "CODE_NAME")
public class CodeNameSqlServer {
    
    public CodeNameSqlServer() {
    }
    
    public CodeNameSqlServer(String id, String value, String lang, Long sortOrder, String name,
            String shortName, String nameWithValue, String option01) {
        this.id = id;
        this.value = value;
        this.lang = lang;
        this.sortOrder = sortOrder;
        this.name = name;
        this.shortName = shortName;
        this.nameWithValue = nameWithValue;
        this.option01 = option01;
    }

    @Id
    @Column(name = "CODE_ID02", length = 4, nullable = false)
    public String id;
    
    @Id
    @Column(name = "VALUE", length = 10, nullable = false)
    public String value;
    
    @Id
    @Column(name = "LANG", length = 2, nullable = false)
    public String lang;
    
    @Column(name = "SORT_ORDER", length = 2, nullable = false)
    public Long sortOrder;
    
    @Column(name = "NAME", length = 200, columnDefinition = "nvarchar(200)")
    public String name;
    
    @Column(name = "SHORT_NAME", length = 200, columnDefinition = "nvarchar(200)")
    public String shortName;
    
    @Column(name = "NAME_WITH_VALUE", length = 200, columnDefinition = "nvarchar(200)")
    public String nameWithValue;
    
    @Column(name = "OPTION01", length = 200, columnDefinition = "nvarchar(200)")
    public String option01;
}
