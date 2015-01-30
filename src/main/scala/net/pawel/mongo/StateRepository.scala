package net.pawel.mongo

import net.pawel.domain.State


trait StateFetcher {
  def state(uuidGenerator: () => String) = StateMongo.findAll.headOption.map(_.toState(uuidGenerator))
}

trait StatePersister {
  def update(state: State) = {
    StateMongo.findAll.foreach(_.delete_!)
    StateMongo.fromState(state).save(true)
  }
}

class StateRepository extends StateFetcher with StatePersister