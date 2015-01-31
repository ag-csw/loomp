<?php
function specialchars_replace($str)
{
	$str = trim($str);
    $i = 0;
    $str2 = "";
    $replace_arr = specialchar_arr();
    while ($i < strlen($str))
    {
         $char = $str[$i];
         $ord = ord($char);

         if ($ord >= 48 && $ord <= 57) $str2 .= $char; // numbers
         if ($ord >= 65 && $ord <= 90) $str2 .= $char; // capital characters
         if ($ord >= 97 && $ord <= 122) $str2 .= $char; // characters

         if (!empty($replace_arr[$ord]))
         {
            $str2 .= $replace_arr[$ord];
         }
        $i++;
    }
    $str2 = strtolower($str2);
 	return $str2;
}

function specialchar_arr()
{
    $r = array();
    $return = array();

    $r[32] = "_";
    $r[45] = "_";
    $r[46] = ".";

    $r[64] = "at";
    $r[138] = "S";
    $r[140] = "OE";
    $r[142] = "Z";
    $r[154] = "s";
    $r[156] = "oe";
    $r[158] = "z";

    $r[192] = "A";
    $r[193] = "A";
    $r[194] = "A";
    $r[195] = "A";
    $r[196] = "A";
    $r[197] = "A";
    $r[198] = "AE";
    $r[199] = "C";
    $r[200] = "E";
    $r[201] = "E";
    $r[202] = "E";
    $r[203] = "E";
    $r[204] = "I";
    $r[205] = "I";
    $r[206] = "I";
    $r[207] = "I";
    $r[208] = "D";
    $r[209] = "N";
    $r[210] = "O";
    $r[211] = "O";
    $r[212] = "O";
    $r[213] = "O";
    $r[214] = "Oe";
    $r[216] = "O";
    $r[217] = "U";
    $r[218] = "U";
    $r[219] = "U";
    $r[220] = "Ue";
    $r[221] = "Y";
    $r[222] = "p";
    $r[223] = "ss";
    $r[224] = "a";
    $r[225] = "a";
    $r[226] = "a";
    $r[227] = "a";
    $r[228] = "ae";
    $r[229] = "a";
    $r[230] = "ae";
    $r[231] = "c";
    $r[232] = "e";
    $r[233] = "e";
    $r[234] = "e";
    $r[235] = "e";
    $r[236] = "i";
    $r[237] = "i";
    $r[238] = "i";
    $r[239] = "i";
    $r[240] = "d";
    $r[241] = "n";
    $r[242] = "o";
    $r[243] = "o";
    $r[244] = "o";
    $r[245] = "o";
    $r[246] = "oe";
    $r[248] = "o";
    $r[249] = "u";
    $r[250] = "u";
    $r[251] = "u";
    $r[252] = "ue";
    $r[253] = "y";
    $r[254] = "p";
    $r[255] = "y";

    $return = $r;
    return $return;
}

?>