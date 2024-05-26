package com.oop.service.sorter;

import java.util.List;

public interface ISorter<T extends Comparable<T>> {
    List<T> sort(List<T> list);
}
