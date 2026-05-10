package com.sy.side.stock.application.port.out;

import com.sy.side.stock.dto.response.EtfPriceInfoItem;
import java.util.List;

public interface LoadEtfPriceInfoPort {
    List<EtfPriceInfoItem> loadEtfPriceInfos(String basDt);
}