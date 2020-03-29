package com.cyprinus.matrix.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public class MatrixBaseEntity implements Serializable {

    @Id
    @GenericGenerator(name = "MongoLikeIdGenerator", strategy = "com.cyprinus.matrix.util.MongoLikeIdGenerator")
    @GeneratedValue(generator = "MongoLikeIdGenerator")
    @Column(name = "_id", length = 26)
    private String _id;

    @JsonIgnore
    @Column(name = "deleted")
    private Integer deleted = 0;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void set_id(MatrixObjectId _id) {
        this._id = _id.toString();
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

}
