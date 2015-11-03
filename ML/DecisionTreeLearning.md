
# Decision Tree Learning

[참고자료](http://www.ke.tu-darmstadt.de/lehre/archiv/ws0809/mldm/dt.pdf)


## 핵심 아이디어 1 : TopDown, Divide-and-Conquer

TopDown

* Root node부터 branch를 만들어서 leaf node까지 top down으로 빌드한다.

Divide-and-Conquer

* Divide into sub-problem
 * Branching을 할 Attribute를 어떤 Heuristics에 의해서 선정한 후 해당 Attribute의 속성 종류별로 Branching을 한다.
* Conquer the problem
 * 각 노드에서의 모든 인스턴스의 분류 정도가 완전했을때, 즉 pure했을 때. 추가적인 branching을 하지 않는다.
 * 분류가 완전하지 않을 때는 다시 Divide 과정을 거친다.


## 핵심 아이디어 2 : Branching heuristics의 선정 기준 Entropy

measurement의 특징
* 각 노드에 속한 인스턴스들이 동일 분류에 소속되 있는지, 아니면 오히려 다양한 분류가 섞여 있는지 => 즉 degree of impurity

Entropy measure : degree of homogeniosity
* 1 => 가장 혼잡도가 낮은 상태, 모든 것이 동일 분류에 속함
* 0.5 => 가장 혼잡도가 높은 상태, all are equally likely.

Average(or Expected) Entropy
* 각 노드별로 entropy는 측정되나, N개의 branch에 대한 종합적인 척도가 필요
* 이것이 Average entropy이다.
* 각 노드별로 측정된 entropy를 weighted sum을 통해 구한다. weight의 기준은 인스턴스의 개수(비율)

Information Gain
* 특정 Attribute를 통해서 N개의 Branching을 한 경우에
* Information Gain = parent node의 entropy - N개 branch의 average entropy
* 이 Gain이 크다는 의미는 branching을 통해서 혼탁도가 더 감소했다는 뜻, 즉 더 분류가 되는 방향으로 진전된다는 뜻, 즉 올바른 학습 방향이라는 뜻
* 후보군이 되는 Attribute마다 이 Gain을 측정하고 Gain이 최대화되는 Attribute를 기준으로 Branching을 한다.

## 핵심 아이디어 3 : Highly-Branch Bias의 사례 및 이를 보완한 Information Gain Ratio

Highly branching problem
* attribute의 속성값이 많은 경우(ex. day, time) 상대적으로 average entropy가 낮을수밖에 없다.
* 그러면 Information Gain이 높아져서 branching이 되는 기준 Attribute로 선정될 가능성이 있다.
* 즉 올바른 학습방향인지 아닌지를 떠나서 속성값의 종류가 많은 방향으로 branching이 진행된다는 것 => Bias

Intrinsic Information
* branch의 개수와 비례적으로 거치는 수치
* 정확한 의미는 [여기 참조](https://en.wikipedia.org/wiki/Information_gain_ratio)

Information Gain Ratio
* 단순한 Gain은 Highly-branch bias문제가 있으므로 branch개수가 많을수록 손해가 되는 것을 가미해준다.
* IGR = (Information Gain)/(Intrinsic Information)

## Entropy의 대항마 : Gini Index

GINI Index
* 1 - sum of p^2
* CART에서는 이 지수를 heuristics로 사용

나머지 사항은 Entropy의 그것과 동일


## Real world에의 고려사항 및 C4.5

현실에서의 상황
* Attribute가 numeric일 수 있다.
* Missing value가 많다.
* noise가 꽤나 있다. noise-robustness가 필요하다.

현실의 상황을 고려하지 않은 ID3 대신 이를 고려한 C4.5 출현
* 오픈소스

돈 받고 파는 것 => C5.0





















