package com.nchu.anti_japan_history.webpage.service.impl;
/**
*@Author: wangshen
*@Date: 2020/4/21
*@Description: 书籍服务类接口实现类
*/
import com.nchu.anti_japan_history.webpage.entity.Books;
import com.nchu.anti_japan_history.webpage.mapper.BookMapper;
import com.nchu.anti_japan_history.webpage.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    BookMapper bookMapper;

    @Override
    public List<Books> bookListAll() {
        return bookMapper.bookListAll();
    }

    @Override
    public int bookSave(Books books) {
        return bookMapper.bookSave(books);
    }
}
