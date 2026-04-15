package com.devops.api.pqr.book;

import com.devops.api.pqr.book.dto.BookOrderResponse;
import com.devops.api.pqr.pqr.entity.Pqr;

public interface BookOrderPort {
    BookOrderResponse notifyBookOrder(Pqr pqr);
}
