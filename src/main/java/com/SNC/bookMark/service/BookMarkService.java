package com.SNC.bookMark.service;

import com.SNC.bookMark.dto.BookMarkDTO;
import com.SNC.bookMark.mapper.BookMarkMapper;
import com.SNC.itemdetail.mapper.ItemDetailMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookMarkService{

    private final ItemDetailMapper itemDetailMapper;
    private final BookMarkMapper bookMarkMapper;

    /**
     * @param itemType,itemId,mbrId,favoriteYN
     * @return
     * @Description 즐찾 insert,update
     */
    public void chngFavorite(String itemId, String itemType, String mbrId, boolean favoriteYN) {
        log.info("itemId:{},itemType:{},mbrId:{},favoriteYN:{}",itemId,itemType,mbrId,favoriteYN);

        String type = resolveType(itemType);
        String yn = favoriteYN ? "Y" : "N";

        if (favoriteYN) {
            if (checkBookMarkHistory(type, itemId, mbrId)) {
                itemDetailMapper.updateBookMark(type, itemId, mbrId, yn);
            } else {
                itemDetailMapper.insertBookMark(type, itemId, mbrId);
            }
        } else {
            itemDetailMapper.updateBookMark(type, itemId, mbrId, yn);
        }
    }

    /**
     * @param itemType,itemId,mbrId
     * @return boolean
     * @Description 해당 아이템 즐찾여부
     */
    public boolean checkBookMark(String itemType,String itemId, String mbrId) {
        int bool = itemDetailMapper.findBookMark(itemType,itemId,mbrId);
        return bool > 0;
    }

    /**
     * @param itemType,itemId,mbrId
     * @return boolean
     * @Description 해당 아이템 즐찾기록 확인
     */
    public boolean checkBookMarkHistory(String itemType,String itemId, String mbrId) {
        int bool = itemDetailMapper.findBookMarkHistory(itemType,itemId,mbrId);
        return bool > 0;
    }

    public String resolveType(String itemType) {
        switch (itemType) {
            case "kr": return "001";
            case "us": return "002";
            case "coin": return "003";
            default: throw new IllegalArgumentException("존재하지 않는 itemType: " + itemType);
        }
    }

    /**
     * @param mbrId
     * @return Map<String, List<String>>
     * @Description 사용자가 즐찾한 모든 아이템 타입별 반환
     */
    public Map<String, List<BookMarkDTO>> getBookmarkItemIdsByType(String mbrId) {
        List<BookMarkDTO> allBookmarks = bookMarkMapper.findAllBookMarkByMember(mbrId);
        return allBookmarks.stream().collect(Collectors.groupingBy(
                BookMarkDTO::getItemType));
    }

}
