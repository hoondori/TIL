
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

## Attribute 속성에 따른 고려사항

Attribute의 속성 종류
* Binary Attribute. ex) Man/Woman
* Nominal vs Ordinal
 * Nominal은 naming이 가능한 하나하나의 독립된 개체. ex) Man/Woman
 * Ordinal은 순서 있는 것. ex) Small/Medium/Large
* Discrete vs Continuous
* Categorical

Binary Split vs MultiWay Split
* Binary split
 * ex) Man/Woman, Seoul/(Dageu,Busan), Length가 1보다 작거나 크거나
* Multiway split
 * Seoul or Dageu or Busan, Length가 (1,2) 구간, (2,3)구간, (3,4)구간 ...

주의사항
* Ordinal의 경우에는 Binary Split의 경우 순서 속성이 어그러지면 안된다.
 * ex) Small/(Medium,Large) 는 맞지만, Medium/(Small,Large) 는 안된다.

Learning시의 속성 종류별 고려사항
* binary split과 multiway split을 모두 해보고 가장 heuristics 수치가 높은 방향으로 전개
 * Binary split의 경우는 경우의 수가 상당히 많을 수 있음
* Continous attribute + binary split
 * 해당 속성을 정렬, O(NlogN)
 * 중간값들을 candidate split points로 보고 각각 heuristics 계산. 가장 높은 것을 택해서 binary split


## Overfitting and Pruning

model이 복잡해지면 overfitting 가능성이 커진다.
단순한 모델이 더 좋다 => Occam's Razor, or principle of pasimony, or minimum description of length principle
적당한 선에서 tree가 너무 커지지 않게 해야 한다. => Pruning

(참고) 왜 overfitting이 생기나?
* due to presence of noise
* due to lack of representative sample
* due to multiple comparision procedure

Pre-pruing vs Post-pruing
* pre-pruning은 학습 데이터에 완전히 fit하기 전이라도 stopping criteria에 의해 tree 성장이 종료
* post-pruning은 일단 학습 데이터에 대해 완전히 fit한 후에 다시 축소해 나가는 과정
* 일반적으로 pre-pruning은 too-early stop to learn 현상이 있어서 post-pruing 선호

Pre-pruning
* chi-square test와 같은 statistical significance test를 이용
* 특정 노드에서 판단컨데 어떤 attribute를 쓰더라도 추가적인 split이 통계적으로 insignificant하다면 추가적 split 중단
* 좋을 때 : 일반적으로 post-pruning보다 빨리 끝나니...
* 최악일 때 : XOR problem과 같은 상황이 있을 때 early stop 현상 발생, 그러나 현실에서 이런 상황 흔치는 않음

Post-pruning
* 핵심 아이디어
 * 일단 trainging error rate가 100% 될 때까지 full-tree로 키워라
 * bottom node부터 각 노드를 simplifiy하여 성능 지표(ex. error rate)가 저하되지 않는 선에서 pruning
* 어떻게 simplify?
 * subtree replacement - subtree 전체 삭제
 * subtree raising - 직속 부모 삭제하고 bubble up
* 어떻게 성능 측정?
 * based on error rate
 * based on confidence interval (C4.5방식)


## Complexity 분석

가정
* M attributes
* N training instances
* tree depth : O(logN) <- binrary decision tree, worst case

Tree Construction => O( MNlogN )
Pruing
* Subtree replacement - O(N)
* Subtree raising - O(N(logN)^2)

Total Cost = O(MNlogN) + O(N(logN)^2)


## Learning시 좀더 Practical한 고려사항들

### Hunt 알고리즘에서 보강사항

* **unseen records 현상**
 * ex) child node에서 Empty Set이 나오는 경우
 * 대처 : parent node에서 major한 class label을 empty set의 label로 지정 (매우 heuristic한 접근)
* **unable to split 현상**
 * ex) node 내에 모인 모든 record들이 동일한 속성들을 가지고 있는데 label은 다른 경우
 * 근본적으로 feature가 부족해서 나온 현상이므로 feature를 추가하는게 맞으나
 * 임기응변적 대처는 unseen records현상 대응처럼 처리

### impurity measure 종류에 너무 집착하지 말라. 성능은 pruning 대응에서 차이가 난다.

### non-rectangular decision boundary 대응

* decision tree는 단일 속성에 대해 split을 하는 경우라면 rectangular decision boundary만 표현하는 한계
* multiple attributes를 고려하여 split을 하는 테크닉을 쓰면 좀 더 complex한 decision boundary를 표현할 수 있다.
* 다만 computationally expensive하다.

### Data fragmentaton 현상 및 대응

* node가 너무 적은 수의 records 를 대표한다.
* 즉 decision region이 너무 파편화되어 너무 많다.
* 대응 : 특정 records 수 이하로 split되지 않도록 threshold를 설정한다.

### Tree Replication 현상 및 대응

* 똑같이 생긴 subtree가 두 개 이상 생기는 현상
* 모델을 해석함에 있어서 매우 난처해지는 상황 => less descriptive for explaining phenomenon
* 대응 : ??

### Redundant 속성 존재에 따른 영향도

* redundant가 있으면 다른 classification learning 기법에서는 일반적으로 성능 저하
* 반면 decision tree는 영향도가 적다. 왜나하면 split시에 duplicate된 두 가지 속성이 둘다 쓰이지 않으므로(??)

### Many irrelavant 속성 존재에 따른 영향도

* irrelavant한 속성이 split 조건으로 쓰이는 대참사 발생. ex) timestamp
* feature selection을 통해 정제된 속성만 사용해야 한다.

## Tree to Rule, Decision List vs Decision Tree

## Regression Tree



































