package com.taeschma.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.taeschma.domain.BinaryFile;

public interface BinaryFileRepository extends CrudRepository<BinaryFile, Long> {
    List<BinaryFile> findByFileName(String fileName);
    @Query("select b from BinaryFile b order by b.lastModified desc")
    List<BinaryFile> findAll();
}