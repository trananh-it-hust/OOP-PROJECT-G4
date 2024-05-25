package com.oop.sorter;

import java.util.List;

public interface ISorter<T extends Comparable<T>> {
    List<T> sort(List<T> list);
}
