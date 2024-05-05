package com.oop.model;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class DetailItem {
    Item item;
    Map<String, List<String>> detailContent;

    // tạo phương thức khởi tạo
    public DetailItem(Integer id) {
        this.item = new Item();
        this.item.setArticleTitle("Title");
        this.item.setArticleType("Type");
        this.item.setArticleLink("Link");
        this.item.setWebsiteSource("Source");
        this.item.setContent(
                "The Federal Reserve does not have to give digital asset bank Custodia access to a master account, a judge ruled on Friday. Federal Reserve Banks have discretion in whether or not to grant master accounts, said Judge Scott Skavdahl in the U.S. District Court in the District of Wyoming in his judgment. The news is a blow to Custodia Bank, which sued the Federal Reserve Board of Governors and the Federal Reserve Bank of Kansas City in 2022 for delaying a decision on its application for a central bank master account. That master account allows institutions direct access to the Fed's payment systems and provides the most direct access to the U.S.'s money supply available to financial institutions. Those without master accounts are often forced to rely on partner banks with master accounts to provide services. 'If Custodia's position was correct, it would effectively mean that every depository institution chartered under the laws of a state, regardless of how soundly crafted, is entitled to a master account allowing it direct access to the federal financial system,' the judge said. According to the court document, Custodia alleged that the Federal Reserve Board violated the Administrative Procedures Act and said its actions were 'arbitrary, capricious, an abuse of discretion, or otherwise not in accordance with the law.' Custodia asked the judge to compel the board to issue a master account. The APA governs how federal agencies develop and issue rules. The court stamped out the APA claim, in part because the board did not take a final action. No master account  Custodia is not statutorily entitled to a master account, the judge said 'The real dispute at the heart of this case is whether FRBKC must grant a master account to Custodia because it was legally eligible or whether FRBKC possessed the discretion to deny Custodia's master account application despite its eligibility,' the judge said. Custodia filed its application with the Kansas City Fed in 2020. In the spring of 2021, the Federal Reserve Board of Governors intervened and sought control of the decision-making process. Custodia's request for a master account was denied in January 2023. Custodia did not immediately respond to a request for comment. Custodia is subject to Wyoming law and is a special purpose depository institution, which are banks that receive deposits and can custody, among other activities. However, they cannot lend 'customer fiat deposits' and have to hold those deposits 100 percent in reserve, according to Custodia's website. ");
        this.item.setCreationDate(null);
        this.detailContent = getDetailContentData(item.getContent());
    }

    public Map<String, List<String>> getDetailContentData(String content) {
        return Map.of("People", List.of("Federal", "Custodia"), "Object", List.of("account", "bank"));
    }

    // getter và setter
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Map<String, List<String>> getDetailContent() {
        return detailContent;
    }

    public void setDetailContent(Map<String, List<String>> detailContent) {
        this.detailContent = detailContent;
    }
}
