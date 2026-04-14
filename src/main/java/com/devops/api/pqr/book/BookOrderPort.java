package com.devops.api.pqr.book;

import com.devops.api.pqr.pqr.entity.Pqr;

public interface BookOrderPort {
    boolean notifyBookOrder(Pqr pqr);
}
