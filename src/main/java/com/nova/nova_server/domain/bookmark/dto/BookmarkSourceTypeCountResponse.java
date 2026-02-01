package com.nova.nova_server.domain.bookmark.dto;

import com.nova.nova_server.domain.cardNews.entity.CardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkSourceTypeCountResponse {
    private Long cardTypeId;
    private String cardTypeName;
    private Long count;

    public BookmarkSourceTypeCountResponse(CardType cardType, Long count) {
        this.cardTypeId = (long) (cardType.ordinal() + 1);
        this.cardTypeName = cardType.name();
        this.count = count;
    }
}