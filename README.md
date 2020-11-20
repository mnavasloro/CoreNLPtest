# ixasrl
Using IXA models for lemmatization, pos tagging, dependencies and  and semantic role labelling (tokenization and sentence splitting from CoreNLP).

Two services available for Spanish:

## POS and LEMMA
> '/pos'
> GET method
> parameter: plain text
> returns json, with POS following the Spanish [EAGLES tagset](https://web.archive.org/web/20160325024315/http://nlp.lsi.upc.edu/freeling/doc/tagsets/tagset-es.html) 

Example for the sentence "Ella come patatas"

`curl -L GET https://ixasrl.linkeddata.es/pos?txt=Ella%20come%20patatas`

Return:
```
{
"annotations":
  [
     {"pos":"PP3FS000","endIndex":4,"beginIndex":0,"lemma":"él","word":"Ella"},
     {"pos":"VMIP3S0","endIndex":9,"beginIndex":5,"lemma":"comer","word":"come"},
     {"pos":"NCFP000","endIndex":17,"beginIndex":10,"lemma":"patata","word":"patatas"}
     ],
   "text":"Ella come patatas"
 }
```



## SRL and deps
> '/srl'
> GET method
> parameter: plain text
> returns [NAF](https://github.com/newsreader/NAF) (xml)

Example for the sentence "Ella come patatas"

`curl -L GET https://ixasrl.linkeddata.es/srl?txt=Ella%20come%20patatas`

Return:
```
{
<?xml version="1.0" encoding="UTF-8"?>
<NAF xml:lang="es" version="1">
  <nafHeader>
    <linguisticProcessors layer="deps">
      <lp name="ixa-pipe-srl-es" beginTimestamp="2020-11-20T18:57:46+0000" endTimestamp="2020-11-20T18:57:46+0000" version="1.0" hostname="protect" />
    </linguisticProcessors>
    <linguisticProcessors layer="srl">
      <lp name="ixa-pipe-srl-es" beginTimestamp="2020-11-20T18:57:46+0000" endTimestamp="2020-11-20T18:57:46+0000" version="1.0" hostname="protect" />
    </linguisticProcessors>
  </nafHeader>
  <text>
    <wf id="w1" offset="0" length="4" sent="1" para="1">Ella</wf>
    <wf id="w2" offset="5" length="4" sent="1" para="1">come</wf>
    <wf id="w3" offset="10" length="7" sent="1" para="1">patatas</wf>
  </text>
  <terms>
    <!--Ella-->
    <term id="t1" type="close" lemma="él" pos="Q" morphofeat="PP3FS000">
      <span>
        <target id="w1" />
      </span>
    </term>
    <!--come-->
    <term id="t2" type="open" lemma="comer" pos="V" morphofeat="VMIP3S0">
      <span>
        <target id="w2" />
      </span>
    </term>
    <!--patatas-->
    <term id="t3" type="open" lemma="patata" pos="N" morphofeat="NCFP000">
      <span>
        <target id="w3" />
      </span>
    </term>
  </terms>
  <deps>
    <!--suj(come, Ella)-->
    <dep from="t2" to="t1" rfunc="suj" />
    <!--cd(come, patatas)-->
    <dep from="t2" to="t3" rfunc="cd" />
  </deps>
  <srl>
    <!--t2 come : arg0[t1 Ella] arg1[t3 patatas]-->
    <predicate id="pr1">
      <!--come-->
      <span>
        <target id="t2" />
      </span>
      <externalReferences>
        <externalRef resource="AnCora" reference="comer.1.default" />
      </externalReferences>
      <role id="rl1" semRole="arg0">
        <!--Ella-->
        <span>
          <target id="t1" head="yes" />
        </span>
      </role>
      <role id="rl2" semRole="arg1">
        <!--patatas-->
        <span>
          <target id="t3" head="yes" />
        </span>
      </role>
    </predicate>
  </srl>
</NAF>
```
