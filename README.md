# ixasrl
Using IXA models for lemmatization, pos tagging, dependencies and  and semantic role labelling (tokenization and sentence splitting from CoreNLP).

Two services available for Spanish:

##POS and LEMMA
'/pos'
GET method
parameter: plain text
returns json

Example for the sentence "Ella come patatas"
'curl -L GET https://ixasrl.linkeddata.es/pos?txt=Ella%20come%20patatas'

e.g. 



##SRL and deps
'/pos'
GET method
parameter: pain text
returns NAF (xml)

Example for the sentence "Ella come patatas"
'curl -L GET https://ixasrl.linkeddata.es/srl?txt=Ella%20come%20patatas'