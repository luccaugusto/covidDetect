# Covid Detect

## Autores
+ [Laura N. de Andrade](https://github.com/lauranandrade)
+ [Lucca A. M. Santos](https://github.com/lrr68)
+ [Richard V. R. Mariano](https://github.com/richvrm)

## Introdução
### Descrição

 O COVID-19 é uma doença infeciosa causada pelo coronavírus da síndrome respiratória aguda grave 2 (SARS-CoV-2). A doença surgiu no final de 2019 e já atingiu mais de 180 países, com mais de 6 milhões de pessoas infectadas no mundo todo. Este trabalho foi desenvolvido como parte da disciplina de Processamento de Imagens. Desenvolvemos um aplicativo para realizar a detecção do vírus em imagens, utilizando técnicas diversas, além de realizar a contagem de vírus contidos naquela imagem. 

 Mais detalhes de implementação e testes estão decritos nesse [artigo](PI_corona.pdf).

### Motivação
A motivação deste trabalho foi colocar em prática o conhecimento adquirido durante o semestre, além de trabalhar um assunto de importância mundial.

### Objetivos
Este trabalho tem como objetivo identificar o coronavírus em meio a várias células a partir da análise de uma imagem aplicando métodos de reconhecimento de padrões e aprendizado de máquina.

## Técnicas implementadas

Para o desenvolvimento desta aplicação foi implementada uma interface gráfica de fácil utilização e manutenção. Dentre as linguagens propostas (C, C++ ou Java), foi escolhida para desenvolver a aplicação a linguagem Java devido a familiaridade e a facilidade de encontrar materiais, como bibliotecas e fóruns, para auxiliar na criação do projeto. Utilizamos diversas bibliotecas da linguagem na implementação, como por exemplo OpenCV.

A janela inicial do sistema possui 8 funcionalidades. São elas: Carregamento de imagem, Seleção, Zoom In, Zoom Out, Restauração de imagem, Limiarização, Rotulação e Detecção, cada uma representada por um botão.

+ Carregamento de imagem - Ao selecionar essa opção, uma janela se abrirá para que você escolha a imagem a ser carregada, em um dos seguintes formatos: PNG, TIFF ou JPG. Após escolher, a imagem é carregada e exibida na tela, e automaticamente um histograma de tons de cinza é calculado. Um histograma mostra a frequência com que um certo valor aparece na amostra. Neste caso, quantas vezes um tom de cinza aparece na imagem escolhida.
+ Seleção - Com esta ferramenta o usuário pode selecionar uma área da imagem. Deve-se clicar em dois pontos, que formaram um retângulo que indica a seleção. A seleção deve ser um exemplo do coronavírus para que a partir dele se possa identificar os outros vírus.
+ Zoom In - Esta aplicação permite que você escolha, através de uma caixa de diálogo, o percentual de zoom que deseja aplicar na imagem.
+ Zoom Out - Análogo ao "Zoom In", porém distancia a imagem.
+ Restauração de imagem - Esta funcionalidade tem como único objetivo restaurar a imagem original caso tenha sido anteriormente limiarizada ou rotulada.
+ Limiarização - A limiarização só pode ser usada quando alguma imagem foi carregada anteriormente. Um algoritmo binário é aplicado, dividindo toda a cor da imagem em apenas dois tons, preto e branco. A partir de uma barra de slider, o usuário pode alterar o ponto desta divisão.
+ Rotulação - Uma rotulação só é possível quando a imagem já foi limiarizada previamente, dessa forma, caso a imagem não esteja limiarizada, a rotulação aciona a função de limiarização e depois rotula. Ela colore cada objeto de uma cor, e o fundo de outra, permitindo assim, a identificação mais precisa de cada objeto.
+ Detecção - Essa função só é liberada após o usuário selecionar na imagem o exemplo de um vírus. Sua única ação é iniciar uma nova janela onde os algoritmos de detecção serão utilizados.


A segunda janela, que é chamada pelo botão de detecção, possui 2 funcionalidades. Algoritmos de Transformada de Hough Elíptica e Correlação Cruzada, cada uma representada por um botão que ao clicado executa o algoritmo e exibe o resultado na tela.

+ Transformada de Hough Elíptica: A transformada de Hough é uma técnica usada para extração de características, usada na análise de imagens, visão computacional e processamento de imagem digital. O objetivo do algoritmo é encontrar formas geométricas em imagens, como retas, círculos e elipses.
    
+ Correlação Cruzada: A correlação cruzada é um artifício usado em processamento de imagens digitais para encontrar, na imagem sendo processada, pedaço(s) que equivalem a uma imagem usada como template.

## Bibliotecas
As bibliotecas utilizadas para o desenvolvimento da aplicação foram:
+ Java Swing: Interface (frames, painéis, botões, label).

+ Java AWT: Captura de eventos.

+ OpenCV: Uma biblioteca desenvolvida em C++, mas está disponível para diversas linguagens de programação, como C, Python, GO e Java. Ela possui as funções de processamento de imagens que utilizamos nos algoritmos de detecção do coronavírus.

+ Luminance: Biblioteca utilizada na limiarização. Calcula a luminância de cada pixel para garantir uma limiarização mais eficiente.

## Algoritmos
Para uma descrição completa dos algoritmos utilizados veja o [artigo](PI_corona.pdf).

## Experimentos e Resultados
Os algoritmos desenvolvidos mostraram um bom desempenho na solução do problema, como podemos ver nas figuras 1 e 2. Estes resultados dependem da melhor escolha de parâmetros e área de seleção.

![THE.PNG](artigo/THE.PNG)
Demonstração do funcionamento do algoritmo Transformada de Hough Elíptica

![CC.PNG](artigo/CC.PNG)
Demonstração do funcionamento do algoritmo Correlação Cruzada
