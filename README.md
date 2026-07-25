```
tobu sync <branch> [stash-name]
        │
        ▼
Validate arguments
        │
        ▼
Get current branch
        │
        ▼
Check uncommitted changes
        │
        ├── Clean
        │    └── Pull
        │
        └── Has changes
             ├── Create stash
             ├── Capture stash reference
             └── Pull
                    │
                    ▼
             Check pull result
                    │
             ┌──────┴──────┐
             │             │
          Failure        Success
             │             │
             ▼             ▼
            STOP      Stash created?
                           │
                      ┌────┴────┐
                      │         │
                     No        Yes
                      │         │
                      │         ▼
                      │    Apply exact stash
                      │         │
                      │    ┌────┴────┐
                      │    │         │
                      │ Success    Conflict
                      │    │         │
                      ▼    ▼         ▼
                     DONE DONE      STOP
```
