package com.dfq.coeffi.E_Learning.repository;

import com.dfq.coeffi.E_Learning.modules.DocumentUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Transactional
public interface DocumentUploadRepository extends JpaRepository<DocumentUpload, Long> {

    @Query("UPDATE DocumentUpload documentupload SET documentupload.status=false where documentupload.id= :id")
    @Modifying
    void deActiveById(@Param("id") long id);

    List<DocumentUpload> findByStatus(boolean status);

    @Query("SELECT documentupload from DocumentUpload documentupload where documentupload.product.id= :id")
    @Modifying
    ArrayList<DocumentUpload> findByProductId(@Param("id") long productId);

    @Query("SELECT doc from DocumentUpload doc where doc.fileName LIKE '%.mp%' ")
    List<DocumentUpload> findAllVideos();

    @Query("SELECT doc from DocumentUpload doc where doc.fileName NOT LIKE '%.mp%' AND doc.product.id= :id ")
    ArrayList<DocumentUpload> findAllDocuments(@Param("id") long productId);

    Optional<DocumentUpload> findById(long fileId);
}
