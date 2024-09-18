package com.nounours.book.common;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {// class to return list of Books continuously not all at once

    private List<T>content;
    private int number;
    private int size;
    private long totalElements;
    private  int totalPages;
    private  boolean first;
    private  boolean last;


}
