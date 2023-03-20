## INFR11199 Advanced Database - Minibase coursework (2022-2023)


### Task2: Strategy to extract join condition from query body and associated evaluation

#### 1. Construct join operator
In terms of join operator logic, there will be multiple relational atom which exists in the query body.
In order to construct the join query hierarchically, a recursive method `constructJoinOperator` in `Operator/common/QueryPlan.java` file
has been designed to go through all relational atoms for building a query plan tree. 
Specifically, the most left relational atom will be used as the left child and the one right after it will be
used as the right child for the first join operator. Then, this join operator will be viewed as the left child 
for the next join operator, and it will move the `rightChildIndex` to the next position to read the next relational atom
as the right child and construct a join operator that could process three relational atoms. Following the same logic,
the next step is to use the join operator which is able to process 3 children as left child, and move the `rightchildIndex`
again to fetch the next relational atom as the right child for constructing the next join operator.

#### 2. Method to extract join condition and selection condition from query body
The two methods with the same name `extractPredicates` (in `Operator/common/QueryPlan.java`) will be used to extract conditions from query body.
One of them will take only one relational atom and whole set of predicates as arguments while another one will
take two relational atoms and entire set of predicates as arguments. In terms of join condition extraction, it will
read two relational atoms, one is for left child, and another one is for right child. If the terms on both sides of
predicates lie in two different relational atoms, then this predicate will be viewed as a join condition.
On the other hand, if they lie in the same relational atom, it will be viewed as the selection condition.

#### 3. Evaluate join condition
##### For explicit predicates
The extracted join conditions will be passed to the constructor when join operator is created.
The left and right relational atoms will be merged and passed to constructor as well. After the next tuple of
both left child and right child are all collected, the predicates of join condition will be checked on the merged tuples
one by one. The details could be viewed on `Operator/JoinOperator.java`.

##### For implicit condition
If the given query follows the format `Q(x, t) :- R(x, y, z), S(x, w, t)`, then we need some strategy to extract the implicit
condition `R.x = S.x`. In my implementation, the nested loop is applied to go through all the terms of every relational atom,
and check if the repeated term exists in different relational atoms. If exists, it will change the name of term of one of the relational atom,
and add a comparison condition on it. For example, we generate a random letter (For instace, `um` here). Then the relational atoms will be like
`R(x, y, z), S(um, w, t)`, and a comparison atom `x = um` will be added to the predicates.

### Task3: Optimization
The idea of optimization is following:

1. Implement **cost-based join**: Hash join and Sort-Merge join, and perform them when it performs better than simple-nested loop join

Evaluation: the performance of hash join is not good as we are expected, the possible reason is that the data in assignment has been stored in a single page,
and the cost on page could be ignored so that the simple nested loop would be more efficient.

3. **Determine join order**: Join two smaller tables first, then join with the one containing larger amount of tuples

Step: 
- Denote size of the number of tuples in all relations after selection operator performed
- Let the table with smaller number of tuples be the left child, and the following larger one as its right child

Evaluation:
The performance of them is also not good as we are expected before the experiment, the reason is that we are using iterator model, and 
only able to aware a single row while processing the `getNextTuple` method, and calculating the number of tuples in a relation needed to go through all tuples which cost a lot.
3. **Cost-based Scan**: Implement (Hash) index-scan and use it when it performs better than fully-table scan (Pending)
