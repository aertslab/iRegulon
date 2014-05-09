iRegulon README
===============

Description
-----------

A regulon consists of a transcription factor (TF) and its direct
transcriptional targets, which contain common TF binding sites in their
cis-regulatory control elements. The iRegulon plugin allows you to identify
regulons using motif and track discovery in an existing network or in a set
of co-regulated genes.

  - Motif discovery is performed in proximal and distal sequences, across ten
    vertebrate genomes, using nearly ten thousand candidate motifs (position
    weight matrices or PWMs).
  - Track discovery allows detecting TFs using gene rankings according to the
    highest ChIP peak within the regulatory space. This version uses more than
    one thousand ChIP-Seq tracks.
  - The output of iRegulon is a list of enriched motifs/tracks, alongside with
    candidate transcription factors. The Motif2TF associations are based on PWM
    annotation, the use of TF homology, and of motif similarity.
  - New networks can be automatically generated based on the predicted
    TF-target interactions.
  - The iRegulon plugin can also be used to query a TF-targets database made of
    high-confidence target genes predicted from the systematic analysis of
    thousands of cancer gene signatures.
  - Supported organisms are human, mouse and fly.

A full version of the plugin including TRANSFAC Professional motifs is provided
from the website http://iregulon.aertslab.org/download.html
(To download the TRANSFAC PRO version, the user will need to have a valid
subscription to TRANSFAC Professional).



Versions
--------

Version 1.2
```````````
Added in this version:
  - track discovery
  - large motif collection with nearly 10K PWMs

Track discovery allows detecting sequence-specific TFs using gene rankings
according to the highest ChIP peak within the regulatory space.

This version uses more than one thousand ChIP-Seq tracks from ENCODE and from
the study of Yan et al. (Yan et al., 2013).


Version 1.1
```````````
First version of iRegulon submitted to Cytoscape App Store:
  - motif collection with more than 6K PWMs
  - no track discovery
