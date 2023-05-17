/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public class IdGeneratorTest {

    @Test
    public void generateWithAccentedChars() {
        Assertions.assertEquals(
            "aaaaaaaaaaaaaaaaaaaaaaaaaaabbbccccccddddddeeeeeeeeeeeeeeeeeeeeeeeeefggggggghhhhhhhiiiiiiiiiiiiiiiijkkkkklllllllmmmnnnnnnnnnoooooooooooooooooooooooooooooooooopprrrrrrrrrsssssssssstttttttuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuvvwwwwwwxxyyyyyyyyyzzzzzzaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbbccccccddddddeeeeeeeeeeeeeeeeeeeeeeeeefggggggghhhhhhhhiiiiiiiiiiiiiiijjkkkkklllllllmmmnnnnnnnnnoooooooooooooooooooooooooooooooooopprrrrrrrrrssssssssssttttttttuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuvvwwwwwwwxxyyyyyyyyyyzzzzzz",
            IdGenerator.generate(
                "ĂẮẶẰẲẴǍÂẤẬẦẨẪÄǞȦǠȀÀẢȂĀĄÅǺḀȺÃÆǼǢꜴꜶꜸꜺꜼḂḄƁḆɃƂĆČÇḈĈĊƇȻĎḐḒḊḌƊḎǲǅĐƋǱǄÉĔĚȨḜÊẾỆỀỂỄḘËĖẸȄÈẺȆĒḖḔĘɆẼḚꝪḞƑǴĞǦĢĜĠƓḠǤḪȞḨĤⱧḦḢḤĦÍĬǏÎÏḮİỊȈÌỈȊĪĮƗĨḬꝹꝻꝽꞂꞄꞆꝬĴɈḰǨĶⱩꝂḲƘḴꝀꝄĹȽĽĻḼḶḸⱠꝈḺĿⱢǈŁǇḾṀṂⱮŃŇŅṊṄṆǸƝṈȠǋÑǊÓŎǑÔỐỘỒỔỖÖȪȮȰỌŐȌÒỎƠỚỢỜỞỠȎꝊꝌŌṒṐƟǪǬØǾÕṌṎȬƢꝎƐƆȢṔṖꝒƤꝔⱣꝐꝘꝖŔŘŖṘṚṜȐȒṞɌⱤꜾƎŚṤŠṦŞŜȘṠṢṨŤŢṰȚȾṪṬƬṮƮŦⱯꞀƜɅꜨÚŬǓÛṶÜǗǙǛǕṲỤŰȔÙỦƯỨỰỪỬỮȖŪṺŲŮŨṸṴꝞṾƲṼꝠẂŴẄẆẈẀⱲẌẊÝŶŸẎỴỲƳỶỾȲɎỸŹŽẐⱫŻẒȤẔƵĲŒᴀᴁʙᴃᴄᴅᴇꜰɢʛʜɪʁᴊᴋʟᴌᴍɴᴏɶᴐᴕᴘʀᴎᴙꜱᴛⱻᴚᴜᴠᴡʏᴢáăắặằẳẵǎâấậầẩẫäǟȧǡạȁàảȃāąᶏẚåǻḁⱥãꜳæǽǣꜵꜷꜹꜻꜽḃḅɓḇᵬᶀƀƃɵćčçḉĉɕċƈȼďḑḓȡḋḍɗᶑḏᵭᶁđɖƌıȷɟʄǳǆéĕěȩḝêếệềểễḙëėẹȅèẻȇēḗḕⱸęᶒɇẽḛꝫḟƒᵮᶂǵğǧģĝġɠḡᶃǥḫȟḩĥⱨḧḣḥɦẖħƕíĭǐîïḯịȉìỉȋīįᶖɨĩḭꝺꝼᵹꞃꞅꞇꝭǰĵʝɉḱǩķⱪꝃḳƙḵᶄꝁꝅĺƚɬľļḽȴḷḹⱡꝉḻŀɫᶅɭłǉẜẛẝḿṁṃɱᵯᶆńňņṋȵṅṇǹɲṉƞᵰᶇɳñǌóŏǒôốộồổỗöȫȯȱọőȍòỏơớợờởỡȏꝋꝍⱺōṓṑǫǭøǿõṍṏȭƣꝏɛᶓɔᶗȣṕṗꝓƥᵱᶈꝕᵽꝑꝙʠɋꝗŕřŗṙṛṝȑɾᵳȓṟɼᵲᶉɍɽↄꜿɘɿśṥšṧşŝșṡṣṩʂᵴᶊȿɡᴑᴓᴝťţṱțȶẗⱦṫṭƭṯᵵƫʈŧᵺɐᴂǝᵷɥʮʯᴉʞꞁɯɰᴔɹɻɺⱹʇʌʍʎꜩúŭǔûṷüǘǚǜǖṳụűȕùủưứựừửữȗūṻųᶙůũṹṵᵫꝸⱴꝟṿʋᶌⱱṽꝡẃŵẅẇẉẁⱳẘẍẋᶍýŷÿẏỵỳƴỷỿȳẙɏỹźžẑʑⱬżẓȥẕᵶᶎʐƶɀ\n"
            )
        );
    }

    @Test
    public void generateWithNonAlphabeticChars() {
        Assertions.assertEquals("", IdGenerator.generate("'\"'(§!)#°}][{_^¨$*€`£=+:/.;,?<>~|\\'"));
    }

    @Test
    public void generateWithSpacesTrimmed() {
        Assertions.assertEquals("test-test", IdGenerator.generate("     test   !   test    "));
    }

    @Test
    public void generateWithInternalSpacesTrimmed() {
        Assertions.assertEquals("test", IdGenerator.generate("test      !"));
    }

    @Test
    public void generateWithLoweredCase() {
        Assertions.assertEquals("test", IdGenerator.generate("TEST"));
    }

    @Test
    public void generateWithDashes() {
        Assertions.assertEquals("test-test", IdGenerator.generate("Test-Test"));
    }

    @Test
    public void generateWithNullValue() {
        Assertions.assertNull(IdGenerator.generate(null));
    }

    @Test
    public void trimTrailingHyphen() {
        Assertions.assertEquals("test", IdGenerator.generate("test-"));
    }
}
