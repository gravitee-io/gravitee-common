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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public class IdGeneratorTest {

    @Test
    public void generateWithAccentedChars() {

        assertEquals("aaaaaaaaaaaaaaaaaaaaaaaaaaabbbccccccddddddeeeeeeeeeeeeeeeeeeeeeeeeefggggggghhhhhhhiiiiiiiiiiiiiiiijkkkkklllllllmmmnnnnnnnnnoooooooooooooooooooooooooooooooooopprrrrrrrrrsssssssssstttttttuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuvvwwwwwwxxyyyyyyyyyzzzzzzaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbbccccccddddddeeeeeeeeeeeeeeeeeeeeeeeeefggggggghhhhhhhhiiiiiiiiiiiiiiijjkkkkklllllllmmmnnnnnnnnnoooooooooooooooooooooooooooooooooopprrrrrrrrrssssssssssttttttttuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuvvwwwwwwwxxyyyyyyyyyyzzzzzz",
                IdGenerator.generate("ĂẮẶẰẲẴǍÂẤẬẦẨẪÄǞȦǠȀÀẢȂĀĄÅǺḀȺÃÆǼǢꜴꜶꜸꜺꜼḂḄƁḆɃƂĆČÇḈĈĊƇȻĎḐḒḊḌƊḎǲǅĐƋǱǄÉĔĚȨḜÊẾỆỀỂỄḘËĖẸȄÈẺȆĒḖḔĘɆẼḚꝪḞƑǴĞǦĢĜĠƓḠǤḪȞḨĤⱧḦḢḤĦÍĬǏÎÏḮİỊȈÌỈȊĪĮƗĨḬꝹꝻꝽꞂꞄꞆꝬĴɈḰǨĶⱩꝂḲƘḴꝀꝄĹȽĽĻḼḶḸⱠꝈḺĿⱢǈŁǇḾṀṂⱮŃŇŅṊṄṆǸƝṈȠǋÑǊÓŎǑÔỐỘỒỔỖÖȪȮȰỌŐȌÒỎƠỚỢỜỞỠȎꝊꝌŌṒṐƟǪǬØǾÕṌṎȬƢꝎƐƆȢṔṖꝒƤꝔⱣꝐꝘꝖŔŘŖṘṚṜȐȒṞɌⱤꜾƎŚṤŠṦŞŜȘṠṢṨŤŢṰȚȾṪṬƬṮƮŦⱯꞀƜɅꜨÚŬǓÛṶÜǗǙǛǕṲỤŰȔÙỦƯỨỰỪỬỮȖŪṺŲŮŨṸṴꝞṾƲṼꝠẂŴẄẆẈẀⱲẌẊÝŶŸẎỴỲƳỶỾȲɎỸŹŽẐⱫŻẒȤẔƵĲŒᴀᴁʙᴃᴄᴅᴇꜰɢʛʜɪʁᴊᴋʟᴌᴍɴᴏɶᴐᴕᴘʀᴎᴙꜱᴛⱻᴚᴜᴠᴡʏᴢáăắặằẳẵǎâấậầẩẫäǟȧǡạȁàảȃāąᶏẚåǻḁⱥãꜳæǽǣꜵꜷꜹꜻꜽḃḅɓḇᵬᶀƀƃɵćčçḉĉɕċƈȼďḑḓȡḋḍɗᶑḏᵭᶁđɖƌıȷɟʄǳǆéĕěȩḝêếệềểễḙëėẹȅèẻȇēḗḕⱸęᶒɇẽḛꝫḟƒᵮᶂǵğǧģĝġɠḡᶃǥḫȟḩĥⱨḧḣḥɦẖħƕíĭǐîïḯịȉìỉȋīįᶖɨĩḭꝺꝼᵹꞃꞅꞇꝭǰĵʝɉḱǩķⱪꝃḳƙḵᶄꝁꝅĺƚɬľļḽȴḷḹⱡꝉḻŀɫᶅɭłǉẜẛẝḿṁṃɱᵯᶆńňņṋȵṅṇǹɲṉƞᵰᶇɳñǌóŏǒôốộồổỗöȫȯȱọőȍòỏơớợờởỡȏꝋꝍⱺōṓṑǫǭøǿõṍṏȭƣꝏɛᶓɔᶗȣṕṗꝓƥᵱᶈꝕᵽꝑꝙʠɋꝗŕřŗṙṛṝȑɾᵳȓṟɼᵲᶉɍɽↄꜿɘɿśṥšṧşŝșṡṣṩʂᵴᶊȿɡᴑᴓᴝťţṱțȶẗⱦṫṭƭṯᵵƫʈŧᵺɐᴂǝᵷɥʮʯᴉʞꞁɯɰᴔɹɻɺⱹʇʌʍʎꜩúŭǔûṷüǘǚǜǖṳụűȕùủưứựừửữȗūṻųᶙůũṹṵᵫꝸⱴꝟṿʋᶌⱱṽꝡẃŵẅẇẉẁⱳẘẍẋᶍýŷÿẏỵỳƴỷỿȳẙɏỹźžẑʑⱬżẓȥẕᵶᶎʐƶɀ\n"));
    }

    @Test
    public void generateWithNonAlphabeticChars() {

        assertEquals("", IdGenerator.generate("'\"'(§!)#°}][{_^¨$*€`£=+:/.;,?<>~|\\'"));
    }

    @Test
    public void generateWithSpacesTrimmed() {

        assertEquals("test-test", IdGenerator.generate("     test   !   test    "));
    }

    @Test
    public void generateWithInternalSpacesTrimmed() {

        assertEquals("test", IdGenerator.generate("test      !"));
    }

    @Test
    public void generateWithLoweredCase() {

        assertEquals("test", IdGenerator.generate("TEST"));
    }

    @Test
    public void generateWithDashes() {

        assertEquals("test-test", IdGenerator.generate("Test-Test"));
    }

    @Test
    public void generateWithNullValue() {

        assertNull(IdGenerator.generate(null));
    }
}
