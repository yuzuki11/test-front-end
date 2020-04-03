package com.cyprinus.matrix.entity;

import com.cyprinus.matrix.type.MatrixBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "Picture")
@Entity
@SQLDelete(sql = "update picture set deleted = 1 where _id = ?")
@Where(clause = "deleted = 0")
public class Picture extends MatrixBaseEntity {

    //访问url
    @Column(name = "url")
    private String url;

    //第一次上传者
    @Column(name = "uploader")
    private String uploader;

    //引用计数
    @JsonIgnore
    @Column(name = "ref")
    private int ref = 0;

    @Column(name = "hash")
    private String hash;

    public void addRef() {
        ref++;
    }

    public void deleteRef() {
        ref--;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public int getRef() {
        return ref;
    }

    public void setRef(int ref) {
        this.ref = ref;
    }


}
